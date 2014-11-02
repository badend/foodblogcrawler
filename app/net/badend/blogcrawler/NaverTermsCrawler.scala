package net.badend.blogcrawler

/**
 * Created by badend on 11/2/14.
 */
object NaverTermsCrawler {

  val url = "http://m.terms.naver.com/listAjax.nhn"
  def param(categoryId:Int=44630, so:String="st1.dsc", page:Int=200, viewType:String="list"
    )={

    Map("categoryId"->s"$categoryId",
    "so" -> s"$so",
    "page" -> s"$page",
    "viewType" -> s"$viewType")
  }
}
