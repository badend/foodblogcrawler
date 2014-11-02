package net.badend.blogcrawler

// Created by dinaa1 on 11/2/14.
object DaumSearchCrawler {

  val url = "http://m.blog.daum.net/_blog/_m/top/searchPost.do"
  def param_post_search(cp:Int=1)={
    Map("query"->"%EC%9A%94%EB%A6%AC",
      "currentPage"->s"$cp",
      "categoryId"->"")
  }
}
