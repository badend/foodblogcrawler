package net.badend.blogcrawler


import java.io.{InputStreamReader, OutputStreamWriter}
import java.net.{HttpURLConnection, URL}
import java.nio.charset.Charset
import java.nio.file.{Paths, Files}
import java.util.concurrent.TimeUnit

import akka.actor.{Props, ActorSystem}
import com.alibaba.fastjson.{JSONObject, JSON}
import org.jsoup.Jsoup

import scala.concurrent.Future

import scala.util.{Failure, Success}
import scala.xml.XML
import akka.io.IO
import akka.pattern.ask
import spray.can.Http
import spray.http._
import spray.client.pipelining._



/**
 * Created by badend on 10/30/14.
 */
object TistoryRunner {
  
  val banedid = Set("dust8864","hagane372","pjsjjanglove","achimjuice","cococo9","ecow","chefstory","ansths","yourlifeplus","getitbyyourhands","ktj-soft","hnewskr","kmkcassis","yourlifeplus","hwarang18se","sart","koreano12324","kblife-story","matzzangs","skhynix","mongmmmomnhg","kmkcassis","banana302","hiroo22","theurgistk","itsmore","livenjoy","onmams","dkfhaldhkd","monster21","dlrmawoek","livenjoy","softroad","banana302","springsunray","worldknowledge3","sejinfng","ccukptok","mari0928","cadceus","yeohwawon","happymonster","roskfldi123","moonstar8","dlfemddlek0001","koreano12324","ksk34g","wingofwolf","nongmin","wbcook","blueoreo","wbcook","madforchoco","dust8864","totalaudio","happymonster","dlfemddlek0001","gabrielle","getitbyyourhands","gabrielle","yeoul012","sart","chinaaz","crw100","jcrown","truereview","rure","totoro1u","hwarang18se","beekeeping","greenhrp","ys06","ramentory","bih4","duqrlwjrdls88","moonstar8","magicnara","happymonster","lotteallsafe","hiroo22","senasama","onmams","greenhrp","mari0928","vampy","paranwater","morocossi","timeeasy","illusion7","dongho1022","lookatmein","kskig1","lara","magicnara","msk8264","flpan","yoon-talk","jch1980","fdfdfag4","eirene88world")

  def main(args:Array[String]) ={
    tistoryProcess
  }

  def tistoryProcess = {
    import Actors._
    import system.dispatcher
    import org.json4s.jackson.Serialization.{read => r, write => w}

    implicit val default = NaverArchiever.formats


    var lastPublished = System.currentTimeMillis()
    val tistoryURLS = Files.newBufferedWriter(Paths.get(s"${System.getProperty("user.dir")}/data/tistory/tistoryURLS.${DaumArchiever.fm.print(System.currentTimeMillis())}"), Charset.forName("UTF8"))
    var cnt = 0
    while (cnt<10000) {
      var lastp = lastPublished
      val data = TistoryCrawler.param(lastPublished = lastPublished, first = false)
      println(data)
      val tistoryurl = s"${TistoryCrawler.url}?${data.map(x => x._1 + "=" + x._2).mkString("&")}"
      try {
        val s = scala.io.Source.fromURL(new URL(tistoryurl))(Charset.forName("UTF8")).mkString
        val parsedData = tistoryJson(s)
        if (parsedData != null) {
          if (parsedData._1 != null) {
            lastPublished = parsedData._1
          } else {
            println("last is null")
            lastPublished = lastPublished - 10000000
          }
          parsedData._2.foreach(x => {

            try {

              val a: BlogPost = TistoryArchiever.tistoryParse(x)
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
            tistoryURLS.write(x)
            tistoryURLS.newLine()
            cnt = cnt + 1
          })*/
        } else {
          println("last is null")
          lastPublished = lastPublished - 10000000
        }



      }


      while (lastp == lastPublished) {
        Thread.sleep(10)
      }

      tistoryURLS.flush()


    }
  }

  def tistoryJson(str:String)={
    println(str)
    val json = JSON.parse(str)
    val data = json.asInstanceOf[JSONObject].get("data").asInstanceOf[JSONObject]
    val lastPublished = data.getLong("lastPublished")
    val list = data.get("list")

    println(lastPublished)



    val jsoup = Jsoup.parse(list.toString)

    val links = jsoup.select("a[href]")

    //println(links)
    import scala.collection.JavaConversions._
    val hrefs = for(link <- links) yield {
      link.attr("href")
    }

    (lastPublished, hrefs.toSeq)
    //links.iterator().toIterator.foreach(x=>x.attr("href"))



  }
}
