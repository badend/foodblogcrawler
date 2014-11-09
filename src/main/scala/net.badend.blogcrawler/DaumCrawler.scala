package net.badend.blogcrawler

/**
 * Created by badend on 10/30/14.
 */
object DaumCrawler {

  val url= "http://blog.daum.net/_blog/_top/sub/blogByCategorySubNew.do"

  def param(category:Int=109, list_type:String="recent", page_no:Int): Map[String, String] ={
    Map("category"->s"$category", "list_type" -> list_type,
      "page_no" -> s"$page_no")
  }
}
