package net.badend.blogcrawler

// Created by dinaa1 on 11/2/14.
object NaverSearchCrawler {

  val url = "http://section.blog.naver.com/sub/PostListByDirectory.nhn"
  def param_post_search(cp:Int=1)={
    Map("type"->"post",
      "option.keyword"->"%EC%9A%94%EB%A6%AC",
        "term"-> "",
    "option.startDate"->"",
      "option.endDate" -> "",
        "option.page.currentPage" -> s"$cp",
      "option.orderBy" -> "sim")
  }
}
