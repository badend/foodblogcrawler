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
object MiznetCookRunner {

  def main(args:Array[String]) ={
    miznetcookProcess
  }

  def miznetcookParse(str:String)={


    val jsoup = Jsoup.parse(str)

    val links = jsoup.select("ul.list_snippet1 list_small li.first a")

    import scala.collection.JavaConversions._
    val hrefs = for(link <- links) yield {
      link.attr("href")
    }
    println(hrefs.mkString(","))
    hrefs

    //(lastPublished, hrefs.toSeq)
    //links.iterator().toIterator.foreach(x=>x.attr("href"))



  }



  def miznetcookProcess = {
    import Actors._
    import system.dispatcher
    var cp = 1
    val pipeline: Future[SendReceive] =
      for (
        Http.HostConnectorInfo(connector, _) <-
        IO(Http) ? Http.HostConnectorSetup("board.miznet.daum.net", port = 80)
      ) yield sendReceive(connector)


    val miznetcookURLS = Files.newBufferedWriter(Paths.get(s"data/miznetcookURLS.${DateTime.now.toIsoDateString}"), Charset.forName("UTF8"))
    var cnt = 0
    var ncp = 0
    while (ncp!=cp) {

      val data = MiznetCookCrawler.param(cp)
      ncp=cp
      println(data)
      val request = Post(MiznetCookCrawler.url, FormData(data))
      val response: Future[HttpResponse] = pipeline.flatMap(_(request))
      response onComplete {
        case Success(s) => {

          miznetcookParse(s.entity.asString).foreach(x => {
            println(x)
            miznetcookURLS.write(x)
            miznetcookURLS.newLine()
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

      miznetcookURLS.flush()


    }
  }

}
