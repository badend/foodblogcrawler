package net.badend.blogcrawler

import java.net.{URLEncoder, URL}
import java.nio.charset.Charset
import java.nio.file.{Files, Paths}

import org.json4s.jackson.Serialization._
import org.jsoup.Jsoup

import scala.io._
import scala.util.Try


import org.json4s.jackson.Serialization.{read => r, write => w}
object NaverArchiever {

  implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all

  def main(args: Array[String]) = {
    val file = if (args.size > 0) args(0) else s"${System.getProperty("user.dir")}/data/naver/naverURLS.${DaumArchiever.fm.print(System.currentTimeMillis())}"
    naverFeeds(file)

  }
  val naverpc = """http://blog.naver.com/(\w+)\??.*""".r
  val naverme = """http://(\w+).blog.me/(\d+)""".r
  val naverme2 = """http://m.blog.naver.com/(\w+)/(\d+).*""".r
  def naverFeeds(file: String) = {
    val murl = for (line <- Source.fromFile(file).getLines()) yield {
      Option(line match {
        case naverpc(domain, docid) => s"http://m.blog.naver.com/$domain/$docid"
        case naverme(domain, docid) => s"http://m.blog.naver.com/$domain/$docid"
        case naverme2(domain, docid) => s"http://m.blog.naver.com/$domain/$docid"
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
        val post =
          Option(try {
            naverParse(url.get)
          }catch{
            case e:Exception => e.printStackTrace()
          })

        if(post.isDefined) {
          val defaultDir = s"${System.getProperty("user.dir")}/data/naver/post"
          val dir = Paths.get(defaultDir)
          Files.createDirectories(dir)
          val wfile = Files.newBufferedWriter(Paths.get(s"$defaultDir/${URLEncoder.encode(url.get, "utf8")}"), Charset.forName("utf8"))


          println(murl)

          wfile.write(w(post))
          wfile.newLine()
          wfile.close()
          Thread.sleep(100)
        }


      }else{
        println(url)
      }
    })
  }
  def naverParse(rurl: String) = {
    var domain:String = null
    var docid:String = null

    val url:String = rurl match {
        case naverpc(domain, docid) => s"http://m.blog.naver.com/$domain/$docid"
        case naverme(domain, docid) => s"http://m.blog.naver.com/$domain/$docid"
        case naverme2(domain, docid) => s"http://m.blog.naver.com/$domain/$docid"
        case _ => {
          println(s"not matched naver mobile url $rurl")
          throw new UnsupportedOperationException(s"네이버 주소가 첨보는건데요? $rurl")
        }
      }


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
      val img_url = image.toString.replace("<span class=\"_img _inl fx\" thumburl=\"", "").replace("\"></span>", "") + "w2"
      println(img_url)

      img_url
    }
    val meterials = IngredientService.ac.find(recipe)
    val met = meterials.groupBy(x=>x.actual).map(x=>(x._1, x._2.head.start)).groupBy(x=>x._2).map(x=>x._2.maxBy(x=>x._1.size)).groupBy(x=>x._1.size + x._2).map(x=>x._2.maxBy(y=>y._1.size))
    println(met)

    new BlogPost(url = url, title = title,
      category = category, date = date,
      ingredient = met.map(x=>x._1).mkString(","), text = recipe,
      images= imgs, id=domain, nickname = username, comment_no=0, like=0)


  }
}
