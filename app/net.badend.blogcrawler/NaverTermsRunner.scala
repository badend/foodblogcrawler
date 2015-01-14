package net.badend.blogcrawler

import java.net.URL
import java.nio.charset.Charset
import java.nio.file.{Paths, Files}

import akka.actor.{Props, ActorSystem}
import com.alibaba.fastjson.{JSONObject, JSON}
import org.jsoup.Jsoup

import scala.concurrent.Future

import scala.util.{Failure, Success}
import akka.io.IO
import akka.pattern.ask
import spray.can.Http
import spray.http._
import spray.client.pipelining._

object NaverTermsRunner {

  def main(args:Array[String]) ={
    naverTermsProcess
  }

  def parsedForNaverTerms(str:String)={
    val jsoup = Jsoup.parse(str)

    val links = jsoup.select("a[href^=/entry.nhn]")

    import scala.collection.JavaConversions._
    val hrefs = for(link <- links) yield {
      link.attr("href")
    }
    println(hrefs.mkString(","))
    hrefs
  }

  def naverTermsProcess() = {
    import Actors._
    import system.dispatcher
    var cp = 1
    val naverURLS = Files.newBufferedWriter(Paths.get(s"${System.getProperty("user.dir")}/data/naver/naver_terms_URLS.${DateTime.now.toIsoDateString}"), Charset.forName("UTF8"))
    var cnt = 0
    while (true) {
      try {

        val data = NaverTermsCrawler.param(page = cp)
        val ncp = cp
        val url = s"${NaverTermsCrawler.url}?${data.map(x => (s"${x._1}=${x._2}")).mkString("&")}"
        println(url)
        val response = scala.io.Source.fromInputStream(new URL(url).openStream(), "utf8").mkString
        //scala.io.Source.fromURL(url, "utf8").mkString
        //println(response)
        parsedForNaverTerms(response).foreach(x => {
          naverURLS.synchronized {
            println(x)
            naverURLS.write(x)
            naverURLS.newLine()

            cnt = cnt + 1
          }

        })


        naverURLS.flush()

        cp = cp + 1

        Thread.sleep(100)
      }
      catch{
        case e:Exception => e.printStackTrace()
      }
    }
    naverURLS.close()
  }

}
