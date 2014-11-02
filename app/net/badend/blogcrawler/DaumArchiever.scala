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

object DaumArchiever {

  def main(args:Array[String]) ={
  //  daumFeeds()
    daumParse()
  }

 /* def daumFeeds() = {
    for (line <- Source.fromFile("daumURLS_2").getLines()) {
      val murl =  line match {
        case daumpc(domain, docid) => s"http://m.blog.daum.com/$domain/$docid"
        case daumme(domain, docid) => s"http://m.blog.daum.com/$domain/$docid"
        case _ => "not matched daum mobile url"
      }
      println(murl)
    }
  }*/

  def daumParse()={
    val html = Source.fromURL("http://m.blog.daum.net/song-poto/1098?t__nil_best=rightimg").mkString
    //println(html)
    val jsoup = Jsoup.parse(html)
    val blogname = jsoup.select("span[class=nick]").text
    val date = jsoup.select("span[class=date]").text
    val username = jsoup.select("div.post_writer strong.writer a").text
    val title = jsoup.select("p[class=title]").text
    val category = jsoup.select("div[class^=head_navi] > h2").html()

    val articleNavi = jsoup.select("div[class=articleNavi]").html("")
    val relation_article = jsoup.select("div#relation_article").html("")

    val recipe = jsoup.select("div#article.small").outerHtml()/*.replace("\n ","\n")
    val recipe_articleNaviRemoved = articleNavi.foldLeft(recipe){
      (recipe_current, an)=>
        println(an)
        (recipe_current.replace(an,""))
    }*/
    /*val summary = jsoup.select("meta._og_tag._description").attr("content")
    val thumbnail = jsoup.select("meta._og_tag._image").attr("content")*/
    val images = jsoup.select("div#article p img.txc-image").toArray


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
