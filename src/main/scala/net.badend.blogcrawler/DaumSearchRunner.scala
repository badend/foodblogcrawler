package net.badend.blogcrawler

import java.nio.charset.Charset
import java.nio.file.{Paths, Files}
import java.util.concurrent.TimeUnit

import akka.actor.{Props, ActorSystem}
import org.jsoup.Jsoup

import scala.concurrent.Future

import scala.util.{Failure, Success}
import scala.xml.XML
import akka.io.IO
import akka.pattern.ask
import spray.can.Http
import spray.http._
import spray.client.pipelining._

// Created by dinaa1 on 11/2/14.
object DaumSearchRunner {

  def main(args:Array[String]) ={
    daumProcessSearch
  }

  def daumParseForSearch(str:String)={
    val jsoup = Jsoup.parse(str)

    val links = jsoup.select("div.search_result_list li.fir a")

    import scala.collection.JavaConversions._
    val hrefs = for(link <- links) yield {
      "m.blog.daum.net" + link.attr("href")
    }
    println(hrefs.mkString(","))
    hrefs
  }

  def daumProcessSearch() = {
    import Actors._
    import system.dispatcher
    var cp = 1
    val pipeline: Future[SendReceive] =
      for (
        Http.HostConnectorInfo(connector, _) <-
        IO(Http) ? Http.HostConnectorSetup("m.blog.daum.net", port = 80)
      ) yield sendReceive(connector)

    val daumURLS = Files.newBufferedWriter(Paths.get(s"data/daum_search_URLS.${DateTime.now.toIsoDateString}"), Charset.forName("UTF8"))
    var cnt = 0
    while (true) {
      val data = DaumSearchCrawler.param_post_search(cp)
      val ncp = cp
      println(data)
      val request = Post(DaumSearchCrawler.url, FormData(data))
      val response: Future[HttpResponse] = pipeline.flatMap(_(request))
      response onComplete {
        case Success(s) => {
          daumParseForSearch(s.entity.asString).foreach(x => {
            daumURLS.synchronized {
              println(x)
              daumURLS.write(x)
              daumURLS.newLine()

              cnt = cnt + 1
            }

          })
          cp=cp+1
        }
        case Failure(f) => println(f)
        case _ => println("ERROR")
      }

      while (cp == ncp) {
        Thread.sleep(10)
      }

      daumURLS.flush()
    }
  }

}
