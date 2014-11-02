package net.badend.blogcrawler

/**
 * Created by badend on 11/2/14.
 */
object MiznetCookCrawler {

  val url = "http://board.miznet.daum.net/gaia/do/cook/recipe/mizr/list"


  def param(pageIndex:Int, bbsId:String="MC001") = {
    Map("pageIndex" -> pageIndex.toString, "bbsId" -> bbsId)
  }

}