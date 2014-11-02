package net.badend.blogcrawler

import java.nio.charset.Charset
import java.nio.file.{Files, Paths}

import akka.io.IO
import akka.pattern.ask
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import spray.client.pipelining._
import spray.http._

import scala.concurrent.Future
import scala.util.{Random, Failure, Success}
import scala.io._
import scala.util.regexp._

object MiznetCookArchiever {

  def main(args:Array[String]) ={
    (1 to 63168).toList.foreach({
      n => miznetcookParse(s"http://board.miznet.daum.net/gaia/do/cook/recipe/mizr/read?articleId=$n&bbsId=MC001&pageIndex=1")
    })

  }


  def miznetcookParse(url:String)={
    val html = Source.fromURL(url, "UTF8").mkString
    val jsoup = Jsoup.parse(html)
    val blogname = jsoup.select("div.feature_etc p.etc a").text


    val _cate = jsoup.select("dt[class=first category]")

    val category = _cate.get(0).nextElementSibling()

    val level = category.nextElementSibling().nextElementSibling().nextElementSibling().nextElementSibling().text()

    val date = jsoup.select("span.date").text()

    val title = jsoup.select("div.feature_etc h3 a").text
    val images = jsoup.select("div.tx-content-container img").toArray
    val recipe = jsoup.select("div.post_ct#viewTypeSelector").text

    val meterials1 = jsoup.select("dt[class=first stuff]").get(0).nextElementSibling()

    val meterials2 = meterials1.nextElementSibling().nextElementSibling()
    val tx_content_container = jsoup.select("div.tx-content-container").outerHtml()

    val met1 = meterials1.text()
    val met2 = meterials2.text()

    println(tx_content_container)
    println(level)
    println(met1)
    println(met2)
    println(category.text())
    println(blogname)
    println(date)
    println(title)
    for (image <- images) {
      val img_url = image.asInstanceOf[Element].attr("src")
      println(img_url)
    }
    println(recipe)
  }
}
