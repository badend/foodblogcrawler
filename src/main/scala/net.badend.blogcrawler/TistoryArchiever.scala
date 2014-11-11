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

object TistoryArchiever {

  def main(args:Array[String]) ={
    tistoryFeeds()
    tistoryParse()
  }

  def tistoryFeeds() = {
     for (line <- Source.fromFile("tistoryURLS_2").getLines()) {
       val murl =  line match {
         case tistorypc(domain, docid) => s"http://m.blog.tistory.com/$domain/$docid"
         case tistoryme(domain, docid) => s"http://m.blog.tistory.com/$domain/$docid"
         case _ => "not matched tistory mobile url"
       }
       println(murl)
     }
   }

  def tistoryParse()={
    val html = Source.fromURL("http://heysukim114.tistory.com/m/post/3136").mkString
    //println(html)
    val jsoup = Jsoup.parse(html)
    val blogname = jsoup.select("div#header h1 a#blogTitle").text
    val date = jsoup.select("span.owner_info span.datetime").text
    jsoup.select("span.owner_info span.datetime").remove()

    val title = jsoup.select("div.area_tit h2 a").text
    jsoup.select("div.area_tit h2 a").remove()
    val category = jsoup.select("span.owner_info span.category_info").text
    jsoup.select("span.owner_info span.category_info").remove()
    jsoup.select("span.owner_info span.txt_bar").remove()
    val username = jsoup.select("span.owner_info").text()


    val recipe = jsoup.select("div.area_content").outerHtml()
    /*val summary = jsoup.select("meta._og_tag._description").attr("content")
    val thumbnail = jsoup.select("meta._og_tag._image").attr("content")*/
    val images = jsoup.select("div.area_content img[class^=item_image able_slideshow]").toArray


    println(category)
    println(blogname)
    println(date)
    println(username)
    println(title)
    /*println(summary)
    println(thumbnail)*/
    println(recipe)
    for (image <- images) {
      val img_url = image.asInstanceOf[Element].attr("src")//.replace("<span class=\"_img _inl fx\" thumburl=\"", "").replace("\"></span>","")
      println(img_url)
    }

  }
}
