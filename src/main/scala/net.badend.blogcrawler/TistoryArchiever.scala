package net.badend.blogcrawler

import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.file.{StandardOpenOption, Files, Paths}

import akka.io.IO
import akka.pattern.ask
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import spray.can.Http
import spray.client.pipelining._
import spray.http._

import scala.concurrent.Future
import scala.util.{Try, Failure, Success}
import scala.io._
import scala.util.regexp._

import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{read => r, write => w}

object TistoryArchiever {

  implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all

  def main(args:Array[String]) ={
    /*val i = scala.io.Source.fromFile("data/ingredient2").getLines()
    val f = Files.newBufferedWriter(Paths.get("data/ingredient3"), Charset.forName("utf8"), StandardOpenOption.CREATE)
    i.toSet.filter(x=>x.size>1).toSeq.sorted.foreach{x=>f.write(x);f.newLine()}
    f.close*/
    tistoryFeeds(args(0))
//    tistoryParse()
  }

  def tistoryFeeds(file:String) = {

     Source.fromFile(file).getLines().foreach{line =>


       val murl = line.replace("tistory.com/", "tistory.com/m/post/")


       val post = Try{tistoryParse(murl)}.toOption
       if(post.isDefined) {
         val defaultDir = "data/tistory/post"
         val dir = Paths.get(defaultDir)
         Files.createDirectories(dir)
         var filename = URLEncoder.encode(murl, "utf8")
         if(filename.length>200) filename = filename.take(200)
         val wfile = Files.newBufferedWriter(Paths.get(s"$defaultDir/${filename}"), Charset.forName("utf8"))


         println(murl)

         wfile.write(w(post))
         wfile.newLine()
         wfile.close()
       }else{
         println(s"ERR URL ${murl}")
       }



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
    val meterials = IngredientService.ac.find(recipe)
    val met = meterials.groupBy(x=>x.actual).map(x=>(x._1, x._2.head.start)).groupBy(x=>x._2).map(x=>x._2.maxBy(x=>x._1.size)).groupBy(x=>x._1.size + x._2).map(x=>x._2.maxBy(y=>y._1.size))

    new BlogPost(url = url, title = title,
      category = category, date = date,
      ingredient = met.map(x=>x._1).mkString(","), text = recipe,
      images= imgs, id=username, nickname = username, comment_no=0, like=0)
  }
}
