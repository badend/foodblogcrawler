package net.badend.blogcrawler

import java.io.{InputStreamReader, OutputStreamWriter}
import java.net.{HttpURLConnection, URL}
import java.nio.charset.Charset
import java.nio.file.{Paths, Files}

import org.json4s.jackson.Serialization._
import org.jsoup.Jsoup

import scala.util.{Failure, Success}
import spray.http._


/**
 * Created by badend on 10/30/14.
 */
object DaumRunner {

  val banedid = Set("nj0090","kshi302","daegupc","huimangcho","affa987","amdsusan","huimangcho","don3400","nj0090","teenz2","gusnl777","dajon","jangchief","rmwldi178","huimangcho","nanherb","jupak21","corngrill","don3400","hrs1350","psh2084","rmwldi178","hskbd","dogood4")
  def main(args:Array[String]) ={
    daumProcess
  }

  def daumParse(str:String)={


    val jsoup = Jsoup.parse(str)

    val links = jsoup.select("ul[class=list_blog_type2] > li > a[href^=http]")

    import scala.collection.JavaConversions._
    val hrefs = for(link <- links) yield {
      link.attr("href")
    }
    println(hrefs.mkString(","))
    hrefs

    //(lastPublished, hrefs.toSeq)
    //links.iterator().toIterator.foreach(x=>x.attr("href"))



  }
  def daumProcess = {
    var cp = 1

    import org.json4s.jackson.Serialization.{read => r, write => w}

    implicit val default = NaverArchiever.formats

    import Actors._
    val daumURLS = Files.newBufferedWriter(Paths.get(s"${System.getProperty("user.dir")}/data/daum/daumURLS.${DaumArchiever.fm.print(System.currentTimeMillis())}"), Charset.forName("UTF8"))
    var ncp = -1
    var cnt = 0
    while (ncp!=cnt) {

      val data = DaumCrawler.param(page_no=cp).map(x=>s"${x._1}=${x._2}").mkString("&")
      ncp=cnt
      println(data)
      val url = s"${DaumCrawler.url}?$data"
      println(url)
      val request = scala.io.Source.fromURL(url).mkString
      daumParse(request).foreach(x => {

        try {
          val a: BlogPost = DaumArchiever.daumParse(x)
          if(a.text.length>0 && !banedid.contains(a.id)) {
            val url = new URL("http://gourmetmarket.co/api/recipe/insert")
            val json = w(a)
            val con = url.openConnection().asInstanceOf[HttpURLConnection]



            println(json)
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            con.setRequestProperty("Content-Length", json.length.toString)

            con.setRequestMethod("POST")
            con.setDoInput(true)
            con.setDoOutput(true)
            con.connect()
            val os = con.getOutputStream()
            val bs = new OutputStreamWriter(os)

            bs.write(json)
            bs.flush()
            bs.close()
            os.close()
            val is = new InputStreamReader(con.getInputStream)

            scala.io.Source.fromInputStream(con.getInputStream)(Charset.forName("UTF8")).getLines().foreach(println _)

            is.close()
          }
        }catch{
          case e:Exception => e.printStackTrace()
        }
        cnt = cnt + 1
      })/*.foreach(x => {
        daumURLS.write(x)
        daumURLS.newLine()
        println(x)
        cnt = cnt + 1

      })*/
      cp = cp + 1

      //daumURLS.flush()


    }
  }


}
