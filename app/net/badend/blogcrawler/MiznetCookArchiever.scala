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
import scala.util.{Random, Failure, Success}
import scala.io._
import scala.util.regexp._

object MiznetCookArchiever {

  def main(args:Array[String]) ={
    Random.shuffle((1 to 163168).toList).take(1).foreach({
      n => miznetcookParse(s"http://board.miznet.daum.net/gaia/do/cook/recipe/mizr/read?articleId=$n&bbsId=MC001&pageIndex=1")
    })

  }


  def miznetcookParse(url:String)={
    val html = Source.fromURL(url, "UTF8").mkString
    val jsoup = Jsoup.parse(html)
    val blogname = jsoup.select("div#_post_property").attr("blogName")
    val category =jsoup.select("a[class=_categoryName]").text
    val date = jsoup.select("div#_post_property").attr("addDate")
    val username = jsoup.select("div.post_writer strong.writer a").text
    val title = jsoup.select("div.feature_etc p.etc a").text
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
