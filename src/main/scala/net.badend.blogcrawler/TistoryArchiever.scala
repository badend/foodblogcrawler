package net.badend.blogcrawler

import java.net.URLEncoder
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

import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{read => r, write => w}

object TistoryArchiever {

  implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all

  def main(args:Array[String]) ={
    tistoryFeeds()
//    tistoryParse()
  }

  def tistoryFeeds() = {
    val dt = "2014-11-11"
     Source.fromFile(s"data/tistoryURLS.${dt}").getLines().take(1).foreach{line =>


       val murl = line.replace("tistory.com/", "tistory.com/m/post/")
       println(murl)

       val defaultDir = "data/tistory/post"
       val dir = Paths.get(defaultDir)
       Files.createDirectories(dir)
       val wfile = Files.newBufferedWriter(Paths.get(s"$defaultDir/${URLEncoder.encode(murl, "utf8")}"), Charset.forName("utf8"))
       val post = tistoryParse(murl)


       wfile.write(w(post))
       wfile.close()



     }
   }

  def tistoryParse(url:String)={
    val html = Source.fromURL(url).mkString
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


    //println(category)
    //println(blogname)
    //println(date)
    //println(username)
    //println(title)
    /*println(summary)
    println(thumbnail)*/
    //println(recipe)
    val imgs = for (image <- images) yield {
      val img_url = image.asInstanceOf[Element].attr("src")//.replace("<span class=\"_img _inl fx\" thumburl=\"", "").replace("\"></span>","")
      //println(img_url)
      img_url
    }

    //val url:String, val title:String, val category:String, val date:String, val ingredient:String, val text:String, val images:Seq[String], val nickname:String, val id:String, val comment_no:Int, val like:Int) {

    new BlogPost(url = url, title = title, category = category, date = date, ingredient = null, text = recipe, images= imgs, id=null, nickname = username, comment_no=0, like=0)
  }
}
