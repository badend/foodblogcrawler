package net.badend.blogcrawler

import java.nio.charset.Charset
import java.nio.file.{Paths, Files}
import java.util.concurrent.TimeUnit

import akka.actor.{Props, ActorSystem}

import scala.util.{Failure, Success}


/**
 * Created by badend on 10/26/14.
 */
object Actors {
  import akka.io.IO
  import akka.pattern.ask

  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures
  implicit val timeout = akka.util.Timeout.apply(100,TimeUnit.SECONDS)








}
