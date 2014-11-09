package net.badend.blogcrawler

/**
 * Created by badend on 10/26/14.
 */
object NaverCrawler {

  val url = "http://section.blog.naver.com/sub/PostListByDirectory.nhn"
  def param(cp:Int=1) = {
    Map("option.page.currentPage"->s"$cp", "option.templateKind"->"0",
      "option.directorySeq"->"20",
    "option.viewType"-> "title",
    "option.orderBy" ->"date" ,
      "option.latestOnly"->"0")
  }

}
