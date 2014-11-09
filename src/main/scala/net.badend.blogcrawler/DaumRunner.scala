package net.badend.blogcrawler

import java.nio.charset.Charset
import java.nio.file.{Paths, Files}

import org.jsoup.Jsoup

import scala.util.{Failure, Success}
import spray.http._


/**
 * Created by badend on 10/30/14.
 */
object DaumRunner {

  def main(args:Array[String]) ={
    daumProcess
  }

  def daumParse(str:String)={


    val jsoup = Jsoup.parse(str)

    val links = jsoup.select("ul[class=list_blog_type2] > li > a[href^=http]")

    import scala.collection.JavaConversions._
    val hrefs = for(link <- links) yield {
      link.attr("href")
    }
    println(hrefs.mkString(","))
    hrefs

    //(lastPublished, hrefs.toSeq)
    //links.iterator().toIterator.foreach(x=>x.attr("href"))



  }
  def daumProcess = {
    import Actors._
    import system.dispatcher
    var cp = 1


    val daumURLS = Files.newBufferedWriter(Paths.get(s"data/daumURLS.${DateTime.now.toIsoDateString}"), Charset.forName("UTF8"))
    var ncp = -1
    var cnt = 0
    while (ncp!=cnt) {

      val data = DaumCrawler.param(page_no=cp).map(x=>s"${x._1}=${x._2}").mkString("&")
      ncp=cnt
      println(data)
      val url = s"${DaumCrawler.url}?$data"
      println(url)
      val request = scala.io.Source.fromURL(url).mkString
      daumParse(request).foreach(x => {
        daumURLS.write(x)
        daumURLS.newLine()
        println(x)
        cnt = cnt + 1

      })
      cp = cp + 1

      daumURLS.flush()


    }
  }


}
