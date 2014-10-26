package net.badend.blogcrawler

import akka.actor.ActorSystem

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

}
