package net.badend.blogcrawler

import java.nio.charset.Charset
import java.nio.file.{Paths, Files}
import java.util.concurrent.TimeUnit

import akka.actor.{Props, ActorSystem}
import com.alibaba.fastjson.{JSONObject, JSON}
import org.jsoup.Jsoup

import scala.concurrent.Future

import scala.util.{Failure, Success}
import scala.xml.XML


/**
 * Created by badend on 10/26/14.
 */
object Actors {
  import akka.io.IO
  import akka.pattern.ask
  import spray.can.Http
  import spray.http._
  import spray.client.pipelining._


  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures
  implicit val timeout = akka.util.Timeout.apply(100,TimeUnit.SECONDS)








}
