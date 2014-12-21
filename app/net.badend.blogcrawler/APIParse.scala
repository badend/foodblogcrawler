package net.badend.blogcrawler

import java.util.concurrent.TimeUnit

import com.google.common.cache.{CacheLoader, CacheBuilder}
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{read => r, write => w}

import org.json4s.JsonDSL._

import org.json4s.jackson.JsonMethods._
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

import scala.concurrent.duration.Duration
import scala.concurrent.{Future, Await}
import scala.util.Try



/**
 * Created by badend on 12/15/14.
 */
object APIParse extends Controller {
  implicit val formats = Serialization.formats(NoTypeHints)


  def parseApi(blog:String, url:String) = Action { implicit req =>


    val parsed = blog match {

      case "tistory" =>{
        TistoryArchiever.tistoryParse(url)

      }
      case "daumblog" =>{
        println(s"daumblog $url")
        DaumArchiever.daumParse(url)

      }
      case "naverblog" =>{
        NaverArchiever.naverParse(url)
    }
      case _ =>{
        throw new UnsupportedOperationException("지원하지 않는 블로")
      }


  }
    Ok(w(parsed))
  }
}
