package net.badend.blogcrawler

import java.nio.charset.Charset
import java.nio.file.{Files, Paths}

import akka.io.IO
import akka.pattern.ask
import org.jsoup.Jsoup
import spray.can.Http
import spray.client.pipelining._
import spray.http._

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.io._
import scala.util.regexp._

object NaverArchiever {

  def main(args:Array[String]) ={
    naverFeeds()
    naverParse()
  }

  def naverFeeds() = {
    val naverpc = """http://blog.naver.com/(\w+)\?.*&logNo=(\d+).*""".r
    val naverme = """http://(\w+).blog.me/(\d+)""".r
    for (line <- Source.fromFile("naverURLS_2").getLines()) {
      val murl =  line match {
          case naverpc(domain, docid) => s"http://m.blog.naver.com/$domain/$docid"
          case naverme(domain, docid) => s"http://m.blog.naver.com/$domain/$docid"
          case _ => "not matched naver mobile url"
      }
      println(murl)
    }
  }

  def naverParse()={
    val html = Source.fromFile("cocodoc.220161519279").mkString
    val jsoup = Jsoup.parse(html)
    val blogname = jsoup.select("div#_post_property").attr("blogName")
    val date = jsoup.select("div#_post_property").attr("addDate")
    val username = jsoup.select("div.post_writer strong.writer a").text
    val title = jsoup.select("div.tit_area h3.tit_h3").text
    val summary = jsoup.select("meta._og_tag._description").attr("content")
    val thumbnail = jsoup.select("meta._og_tag._image").attr("content")
    val images = jsoup.select("div.post_ct span._img._inl").toArray
    val recipe = jsoup.select("div.post_ct#viewTypeSelector").text

    println(blogname)
    println(date)
    println(username)
    println(title)
    println(summary)
    println(thumbnail)
    for (image <- images) {
      val img_url = image.toString.replace("<span class=\"_img _inl fx\" thumburl=\"", "").replace("\"></span>","")
      println(img_url)
    }
    println(recipe)
  }
}
