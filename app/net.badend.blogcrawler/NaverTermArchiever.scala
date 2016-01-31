package net.badend.blogcrawler

import java.nio.charset.Charset
import java.nio.file.{Files, Paths}

import akka.io.IO
import akka.pattern.ask
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import spray.can.Http
import spray.client.pipelining._
import spray.http._

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.io._

object NaverTermsArchiever {

  def main(args:Array[String]) ={
    ntFeeds()
  }

  def ntFeeds() = {

    val ntw = Files.newBufferedWriter(Paths.get(s"${System.getProperty("user.dir")}/data/naver/naverterms.${DateTime.now.toIsoDateString}"), Charset.forName("UTF8"))

    Source.fromFile(s"${System.getProperty("user.dir")}/data/naver_terms_URLS.2014-11-15").getLines().foreach{line =>
      try {
        val nt = ntParse(s"http://m.terms.naver.com$line")
        if(nt!=null) {

          ntw.write(nt._1)
          ntw.write("\t")
          ntw.write(nt._2)
          ntw.newLine()
        }else{

          println(line)
        }
      }catch{
        case e:Exception =>
          e.printStackTrace()
      }
     }
    ntw.close()
   }

  def ntParse(url:String)={
    var html = ""
    try {
      html = Source.fromURL(url).mkString.replace("보기 설정", "")


      val jsoup = Jsoup.parse(html)
      val foodname = jsoup.select("h1").text
      val meterials = jsoup.select("p > strong:contains(재료)").parents().first().text().replaceAll("· \\S재료 ", "")

      (foodname, meterials)
    }  catch{
      case e:Exception => e.printStackTrace()
        null
    }

  }
}
