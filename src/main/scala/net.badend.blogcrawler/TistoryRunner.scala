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
object TistoryRunner {

  def main(args:Array[String]) ={
    tistoryProcess
  }

  def tistoryProcess = {
    import Actors._
    import system.dispatcher
    val pipeline: Future[SendReceive] =
      for (
        Http.HostConnectorInfo(connector, _) <-
        IO(Http) ? Http.HostConnectorSetup("www.tistory.com", port = 80)
      ) yield sendReceive(connector)


    var lastPublished = System.currentTimeMillis()
    val tistoryURLS = Files.newBufferedWriter(Paths.get(s"data/tistoryURLS.${DateTime.now.toIsoDateString}"), Charset.forName("UTF8"))
    var cnt = 0
    while (true) {
      var lastp = lastPublished
      val data = TistoryCrawler.param(lastPublished = lastPublished, first = false)
      println(data)
      val request = Post(TistoryCrawler.url, FormData(data))
      val response: Future[HttpResponse] = pipeline.flatMap(_(request))
      response onComplete {
        case Success(s) => {
          val parsedData = tistoryJson(s.entity.asString)
          if (parsedData != null) {
            if (parsedData._1 != null) {
              lastPublished = parsedData._1
            } else {
              println("last is null")
              lastPublished = lastPublished - 10000000
            }
            parsedData._2.foreach(x => {
              tistoryURLS.write(x)
              tistoryURLS.newLine()
              cnt = cnt + 1
            })
          } else {
            println("last is null")
            lastPublished = lastPublished - 10000000
          }

        }
        case Failure(f) => println(f)
        case _ => println("ERROR")
      }

      while (lastp == lastPublished) {
        Thread.sleep(10)
      }

      tistoryURLS.flush()


    }
  }

  def tistoryJson(str:String)={
    println(str)
    val json = JSON.parse(str)
    val data = json.asInstanceOf[JSONObject].get("data").asInstanceOf[JSONObject]
    val lastPublished = data.getLong("lastPublished")
    val list = data.get("list")

    println(lastPublished)



    val jsoup = Jsoup.parse(list.toString)

    val links = jsoup.select("a[href]")

    //println(links)
    import scala.collection.JavaConversions._
    val hrefs = for(link <- links) yield {
      link.attr("href")
    }

    (lastPublished, hrefs.toSeq)
    //links.iterator().toIterator.foreach(x=>x.attr("href"))



  }
}
