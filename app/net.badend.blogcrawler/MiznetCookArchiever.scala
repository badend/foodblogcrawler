package net.badend.blogcrawler

import java.nio.charset.Charset
import java.nio.file.{Files, Paths}

import akka.io.IO
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import spray.client.pipelining._
import spray.http._

import scala.concurrent.Future
import scala.util.{Random, Failure, Success}
import scala.io._

import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{read => r, write => w}

object MiznetCookArchiever {

  implicit val formats = org.json4s.DefaultFormats
  def main(args:Array[String]) ={


    val miznetcook = Files.newBufferedWriter(Paths.get(s"${System.getProperty("user.dir")}/data/miznet/miznetcookDataS_2.${DateTime.now.toIsoDateString}"), Charset.forName("UTF8"))

    (7000 to 70000).toList.foreach({
      n =>
        var json = ""
        try {

          json = w(miznetcookParse(s"http://board.miznet.daum.net/gaia/do/cook/recipe/mizr/read?articleId=$n&bbsId=MC001&pageIndex=1"))

          println(json)
          miznetcook.write(json)
          miznetcook.newLine()
          miznetcook.flush()
        }catch{
          case e:Exception => e.printStackTrace()
            println(json)
        }
    })
    miznetcook.close()

  }


  case class MisznetCook(title:String, images:Seq[String], blogname:String, category:String, level:String, date:String,
                         recipe:String, met1:String, met2:String)
  def miznetcookParse(url:String)={
    val html = Source.fromURL(url, "UTF8").mkString
    val jsoup = Jsoup.parse(html)
    val blogname = jsoup.select("div.feature_etc p.etc a").text


    val _cate = jsoup.select("dt[class=first category]")

    println(_cate)
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

    val imgs= for (image <- images) yield {
      val img_url = image.asInstanceOf[Element].attr("src")

      img_url
    }
    MisznetCook(
    title=title, images=imgs, blogname=blogname, category=category.text(), level=level, date=date,met1=met1, met2=met2, recipe=recipe
    )
  }
}
