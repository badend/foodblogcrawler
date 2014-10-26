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


/**
 * Created by badend on 10/26/14.
 */
object Actors extends App{
  import akka.io.IO
  import akka.pattern.ask
  import spray.can.Http
  import spray.http._
  import spray.client.pipelining._


  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures
  implicit val timeout = akka.util.Timeout.apply(100,TimeUnit.SECONDS)







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
  def naverParseForSearch(str:String)={


    val jsoup = Jsoup.parse(str)

    val links = jsoup.select("/h5")

    import scala.collection.JavaConversions._
    val hrefs = for(link <- links) yield {
      link.attr("href")
    }
    println(hrefs.mkString(","))
    hrefs

    //(lastPublished, hrefs.toSeq)
    //links.iterator().toIterator.foreach(x=>x.attr("href"))



  }

  def naverProcessPostSearch = {
    var cp = 1
    val pipeline: Future[SendReceive] =
      for (
        Http.HostConnectorInfo(connector, _) <-
        IO(Http) ? Http.HostConnectorSetup("section.blog.naver.com", port = 80)
      ) yield sendReceive(connector)


    val naverURLS = Files.newBufferedWriter(Paths.get("naver_search_URLS"), Charset.forName("UTF8"))
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
            naverURLS.write(x)
            naverURLS.newLine()
            cnt = cnt + 1
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



  def naverProcess = {
    var cp = 1
    val pipeline: Future[SendReceive] =
      for (
        Http.HostConnectorInfo(connector, _) <-
        IO(Http) ? Http.HostConnectorSetup("section.blog.naver.com", port = 80)
      ) yield sendReceive(connector)


    val naverURLS = Files.newBufferedWriter(Paths.get("naverURLS"), Charset.forName("UTF8"))
    var cnt = 0
    while (true) {

      val data = NaverCrawler.param(cp)
      val ncp = cp
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

      while (cp == ncp) {
        Thread.sleep(10)
      }

      naverURLS.flush()


    }
  }

  def tistoryProcess = {
    val pipeline: Future[SendReceive] =
      for (
        Http.HostConnectorInfo(connector, _) <-
        IO(Http) ? Http.HostConnectorSetup("www.tistory.com", port = 80)
      ) yield sendReceive(connector)


    var lastPublished = System.currentTimeMillis()
    val tistoryURLS = Files.newBufferedWriter(Paths.get("tistoryURLS"), Charset.forName("UTF8"))
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

}
