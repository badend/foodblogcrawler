package net.badend.blogcrawler

import org.jsoup.Jsoup
import scala.util.{Failure, Success}
import scala.io._

object DaumArchiever {

  def main(args:Array[String]) ={
    daumFeeds()
    daumParse()
  }

  def daumFeeds() = {
    val daumpc = """http://blog.daum.com/(\w+)\?.*&logNo=(\d+).*""".r
    val daumme = """http://(\w+).blog.me/(\d+)""".r
    for (line <- Source.fromFile("daumURLS_2").getLines()) {
      val murl =  line match {
        case daumpc(domain, docid) => s"http://m.blog.daum.com/$domain/$docid"
        case daumme(domain, docid) => s"http://m.blog.daum.com/$domain/$docid"
        case _ => "not matched daum mobile url"
      }
      println(murl)
    }
  }

  def daumParse()={
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
