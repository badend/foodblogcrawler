package net.badend.blogcrawler

import java.net.URL
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

    val links = jsoup.select("ul[class=list_type_2] li h5 a[onclick^=clickcr(this,]")

    import scala.collection.JavaConversions._
    val hrefs = for(link <- links) yield {
      //println(link.attr("href"))
      link.attr("href")
    }
    println(s"${hrefs.size} collected, ${hrefs.head}")
    hrefs

    //(lastPublished, hrefs.toSeq)
    //links.iterator().toIterator.foreach(x=>x.attr("href"))



  }

  def naverProcess = {
    import Actors._
    import system.dispatcher
    var cp = 1

    val naverURLS = Files.newBufferedWriter(Paths.get(s"data/naverURLS.${DateTime.now.toIsoDateString}"), Charset.forName("UTF8"))
    var cnt = 0

    var ncp = 0
    while (ncp!=cp && cp<=100) {

      val data = NaverCrawler.param(cp)
      ncp = cp
      val naverurl = s"${NaverCrawler.url}?${data.map(x => x._1 + "=" + x._2).mkString("&")}"
      println(naverurl)
      try {
        val s = scala.io.Source.fromURL(new URL(naverurl))(Charset.forName("UTF8")).mkString
        naverParse(s).foreach(x => {
          naverURLS.write(x)
          naverURLS.newLine()
          cnt = cnt + 1
        })
        cp = cp + 1


      }
    catch    {
      case e:Exception => e.printStackTrace()
    }

      while (ncp ==cp) {
        Thread.sleep(10)
      }

      naverURLS.flush()


    }
  }

}
