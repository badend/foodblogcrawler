package net.badend.blogcrawler

import java.nio.charset.Charset
import java.nio.file.{Paths, Files}
import java.util.concurrent.TimeUnit

import akka.actor.{Props, ActorSystem}
import com.alibaba.fastjson.{JSONObject, JSON}
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
object NaverSearchRunner {

  def main(args:Array[String]) ={
    naverProcessPostSearch
  }

  def naverParseForSearch(str:String)={
    val jsoup = Jsoup.parse(str)

    val links = jsoup.select("li.add_img h5 a")

    import scala.collection.JavaConversions._
    val hrefs = for(link <- links) yield {
      link.attr("href")
    }
    println(hrefs.mkString(","))
    hrefs
  }

  def naverProcessPostSearch() = {
    import Actors._
    import system.dispatcher
    var cp = 1
    val pipeline: Future[SendReceive] =
      for (
        Http.HostConnectorInfo(connector, _) <-
        IO(Http) ? Http.HostConnectorSetup("section.blog.naver.com", port = 80)
      ) yield sendReceive(connector)

    val naverURLS = Files.newBufferedWriter(Paths.get(s"naver_search_URLS.${DateTime.now.toIsoDateString}"), Charset.forName("UTF8"))
    var cnt = 0
    while (true) {

      val data = NaverCrawler.param_post_search(cp)
      val ncp = cp
      println(data)
      val request = Post(NaverCrawler.url_post_search, FormData(data))
      val response: Future[HttpResponse] = pipeline.flatMap(_(request))
      response onComplete {
        case Success(s) => {
          naverParseForSearch(s.entity.asString).foreach(x => {
            naverURLS.synchronized {
              naverURLS.write(x)
              naverURLS.newLine()

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

      naverURLS.flush()
    }
  }

}
