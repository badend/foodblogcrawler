package net.badend.blogcrawler

/**
 * Created by badend on 10/21/14.
 */
object TistoryCrawler {


  val url = "http://www.tistory.com/category/getMoreCategoryPost.json"

  def param(category: String = "life%2Frecipe", order: String = "recent", lastPublished: Long=0, first: Boolean)={


    Map("category"->"life/recipe", "order" -> "recent", "lastPublished"->lastPublished.toString, "first"->first.toString)

  }

}
