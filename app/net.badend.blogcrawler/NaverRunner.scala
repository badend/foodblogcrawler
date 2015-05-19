package net.badend.blogcrawler

import java.io.{InputStreamReader, InputStream, OutputStreamWriter}
import java.net.{Proxy, HttpURLConnection, URI, URL}
import java.nio.charset.Charset
import java.nio.file.{Paths, Files}
import java.util.concurrent.TimeUnit

import akka.actor.{Props, ActorSystem}
import com.alibaba.fastjson.{JSONObject, JSON}
import org.jsoup.Jsoup

import scala.concurrent.Future

object NaverRunner {
  val banedid = Set("my_chef","nagaja27","silver877","anyoung55","jylovesj1215","yheo85","sejinfng","wwf256")

  def main(args:Array[String]) ={
    naverProcess
  }

  def naverParse(str:String)={


    val jsoup = Jsoup.parse(str)

    val links = jsoup.select("ul[class=list_type_2] li h5 a[onclick^=clickcr(this,]")

    import scala.collection.JavaConversions._
    val hrefs = for(link <- links) yield {
      //println(link.attr("href"))
      link.attr("href")
    }
    println(s"${hrefs.size} collected, ${hrefs.head}")
    hrefs

    //(lastPublished, hrefs.toSeq)
    //links.iterator().toIterator.foreach(x=>x.attr("href"))



  }


  def naverProcess = {
    implicit val default = NaverArchiever.formats

    import org.json4s.jackson.Serialization._
    import org.jsoup.Jsoup

    import scala.io._
    import scala.util.Try


    import org.json4s.jackson.Serialization.{read => r, write => w}
    import Actors._
    import system.dispatcher


    var cp = 1

    val naverURLS = Files.newBufferedWriter(Paths.get(s"${System.getProperty("user.dir")}/data/naver/naverURLS.${DaumArchiever.fm.print(System.currentTimeMillis())}"), Charset.forName("UTF8"))
    var cnt = 0

    var ncp = 0
    while (ncp!=cp && cp<=100) {

      val data = NaverCrawler.param(cp)
      ncp = cp
      val naverurl = s"${NaverCrawler.url}?${data.map(x => x._1 + "=" + x._2).mkString("&")}"
      println(naverurl)
      try {
        val s = scala.io.Source.fromURL(new URL(naverurl))(Charset.forName("UTF8")).mkString
        naverParse(s).foreach(x => {

          try {

            val a: BlogPost = NaverArchiever.naverParse(x)
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
              con.disconnect()
            }
          }catch{
            case e:Exception => e.printStackTrace()
          }
          cnt = cnt + 1
        })
        cp = cp + 1
      }
    catch    {
      case e:Exception => e.printStackTrace()
    }

      while (ncp ==cp) {
        Thread.sleep(10)
      }

      naverURLS.flush()


    }
  }

}
