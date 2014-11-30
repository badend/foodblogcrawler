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


    println(category)
    println(blogname)
    println(date)
    println(username)
    println(title)
    /*println(summary)
    println(thumbnail)*/
    println(recipe)
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
