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
import scala.util.regexp._

object NaverTermsArchiever {

  def main(args:Array[String]) ={
    //  ntFeeds()
    ntParse()
  }

  /* def ntFeeds() = {
     for (line <- Source.fromFile("ntURLS_2").getLines()) {
       val murl =  line match {
         case ntpc(domain, docid) => s"http://m.blog.nt.com/$domain/$docid"
         case ntme(domain, docid) => s"http://m.blog.nt.com/$domain/$docid"
         case _ => "not matched nt mobile url"
       }
       println(murl)
     }
   }*/

  def ntParse()={
    val html = Source.fromURL("http://m.terms.naver.com/entry.nhn?docId=1988693&cid=48156&categoryId=48156").mkString.replace("보기 설정" ,"")

    val jsoup = Jsoup.parse(html)
    val foodname = jsoup.select("h1").text
    val meterials = jsoup.select("p > strong:contains(재료)").parents().first().text().replaceAll("· \\S재료 ","")

    println(foodname)
    println(meterials)
  }
}
