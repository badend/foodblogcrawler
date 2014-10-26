package net.badend.blogcrawler

/**
 * Created by badend on 10/26/14.
 */
object NaverCrawler {

  val url = "http://section.blog.naver.com/sub/PostListByDirectory.nhn"
  val url_post_search = "http://section.blog.naver.com/sub/SearchBlog.nhn"
  def param_post_search(cp:Int=1)={
    Map("type"->"post",
      "option.keyword"->"%EC%9A%94%EB%A6%AC",
        "term"-> "",
    "option.startDate"->"",
      "option.endDate" -> "",
        "option.page.currentPage" -> s"$cp",
      "option.orderBy" -> "sim")
  }
  def param(cp:Int=1) = {
    Map("option.page.currentPage"->s"$cp", "option.templateKind"->"0",
      "option.directorySeq"->"20",
    "option.viewType"-> "title",
    "option.orderBy" ->"date" ,
      "option.latestOnly"->"0")
  }

}
