package net.badend.blogcrawler

import java.net.URL
import java.nio.charset.Charset

import org.jsoup.Jsoup

import scala.io._

object NaverArchiever {

  def main(args: Array[String]) = {
    val file = if (args.size > 0) args(0) else "data/naverURLS.2014-11-01"
    naverFeeds(file)

  }

  def naverFeeds(file: String) = {
    val naverpc = """http://blog.naver.com/(\w+)\?.*&logNo=(\d+).*""".r
    val naverme = """http://(\w+).blog.me/(\d+)""".r
    val murl = for (line <- Source.fromFile(file).getLines()) yield {
      Option(line match {
        case naverpc(domain, docid) => s"http://m.blog.naver.com/$domain/$docid"
        case naverme(domain, docid) => s"http://m.blog.naver.com/$domain/$docid"
        case _ => {
          println(line)
          println("not matched naver mobile url")
          null
        }
      })
    }
    murl.foreach(url => {
      if(url.isDefined) {
        println(url)
        naverParse(url.get)
      }else{
        println(url)
      }
    })
  }

  def naverParse(url: String) = {
    val html = scala.io.Source.fromURL(new URL(url))(Charset.forName("UTF8")).mkString
    val jsoup = Jsoup.parse(html)
    val blogname = jsoup.select("div#_post_property").attr("blogName")
    val category = jsoup.select("a[class=_categoryName]").text
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
    val imgs = for (image <- images) yield {
      val img_url = image.toString.replace("<span class=\"_img _inl fx\" thumburl=\"", "").replace("\"></span>", "")
      println(img_url)

      img_url
    }
    val meterials = IngredientService.ac.find(recipe)
    val met = meterials.groupBy(x=>x.actual).map(x=>(x._1, x._2.head.start)).groupBy(x=>x._2).map(x=>x._2.maxBy(x=>x._1.size)).groupBy(x=>x._1.size + x._2).map(x=>x._2.maxBy(y=>y._1.size))
    println(met)

    new BlogPost(url = url, title = title,
      category = category, date = date,
      ingredient = met.map(x=>x._1).mkString(","), text = recipe,
      images= imgs, id=username, nickname = username, comment_no=0, like=0)


  }
}
