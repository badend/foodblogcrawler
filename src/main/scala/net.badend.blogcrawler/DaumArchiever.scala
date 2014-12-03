package net.badend.blogcrawler

import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.file.{Files, Paths}

import akka.io.IO
import akka.pattern.ask
import org.json4s.jackson.Serialization._
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import spray.can.Http
import spray.client.pipelining._
import spray.http._

import scala.concurrent.Future
import scala.util.{Try, Failure, Success}
import scala.io._
import scala.util.regexp._

import org.json4s.jackson.Serialization.{read => r, write => w}
object DaumArchiever {

  implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all
  def main(args:Array[String]) ={
    daumFeeds(Try{args(0)}.getOrElse("data/daumURLS.2014-10-30"))

  }

  def daumFeeds(file:String) = {
    for (line <- Source.fromFile(file).getLines()) {
      val murl = line.replace("http://blog.daum.net", "http://m.blog.daum.net")
      println(murl)
      val post = Try{daumParse(murl)}.toOption
      if(post.isDefined) {
        val defaultDir = "data/daum/post"
        val dir = Paths.get(defaultDir)
        Files.createDirectories(dir)
        val wfile = Files.newBufferedWriter(Paths.get(s"$defaultDir/${URLEncoder.encode(murl, "utf8")}"), Charset.forName("utf8"))


        println(murl)

        wfile.write(w(post))
        wfile.newLine()
        wfile.close()
      }




    }
  }

  def daumParse(url:String)={
    val html = Source.fromURL(url).mkString
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

    val imgs = for (image <- images) yield {
      val img_url = image.asInstanceOf[Element].attr("src")//.replace("<span class=\"_img _inl fx\" thumburl=\"", "").replace("\"></span>","")
      println(img_url)
      img_url
    }
    val meterials = IngredientService.ac.find(recipe)
    val met = meterials.groupBy(x=>x.actual).map(x=>(x._1, x._2.head.start)).groupBy(x=>x._2).map(x=>x._2.maxBy(x=>x._1.size)).groupBy(x=>x._1.size + x._2).map(x=>x._2.maxBy(y=>y._1.size))

    new BlogPost(url = url, title = title,
      category = category, date = date,
      ingredient = met.map(x=>x._1).mkString(","), text = recipe,
      images= imgs, id=username, nickname = username, comment_no=0, like=0)
  }
}
