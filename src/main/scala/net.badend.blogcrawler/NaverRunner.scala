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
/**
 * Created by badend on 10/30/14.
 */
object NaverRunner {

  def main(args:Array[String]) ={
    naverProcess
  }

  def naverParse(str:String)={


    val jsoup = Jsoup.parse(str)

    val links = jsoup.select("a[class^=_mouseover _mouseout _toggle _eachClick _updateTopPostViewCount _param]")

    import scala.collection.JavaConversions._
    val hrefs = for(link <- links) yield {
      link.attr("href")
    }
    println(hrefs.mkString(","))
    hrefs

    //(lastPublished, hrefs.toSeq)
    //links.iterator().toIterator.foreach(x=>x.attr("href"))



  }

  def naverProcess = {
    import Actors._
    import system.dispatcher
    var cp = 1
    val pipeline: Future[SendReceive] =
      for (
        Http.HostConnectorInfo(connector, _) <-
        IO(Http) ? Http.HostConnectorSetup("section.blog.naver.com", port = 80)
      ) yield sendReceive(connector)


    val naverURLS = Files.newBufferedWriter(Paths.get(s"data/naverURLS.${DateTime.now.toIsoDateString}"), Charset.forName("UTF8"))
    var cnt = 0
    var ncp = 0
    while (ncp!=cp && cp<=100) {

      val data = NaverCrawler.param(cp)
      ncp=cp
      println(data)
      val request = Post(NaverCrawler.url, FormData(data))
      val response: Future[HttpResponse] = pipeline.flatMap(_(request))
      response onComplete {
        case Success(s) => {

          naverParse(s.entity.asString).foreach(x => {
            naverURLS.write(x)
            naverURLS.newLine()
            cnt = cnt + 1
          })
          cp=cp+1
        }
        case Failure(f) => println(f)
        case _ => println("ERROR")
      }

      while (ncp ==cp) {
        Thread.sleep(10)
      }

      naverURLS.flush()


    }
  }

}
