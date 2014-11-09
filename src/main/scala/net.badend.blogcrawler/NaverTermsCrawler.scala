package net.badend.blogcrawler

/**
 * Created by badend on 11/2/14.
 */
object NaverTermsCrawler {
//curl 'http://api.like.naver.com/likeIt/v1/likeItContentListByTotalCount.jsonp?serviceId=ENCYCLOPEDIA
// &_callback=like._callback&contentsIds=1820014,1988776,1988755,1991361,731611,1988138,1988748,1992829,1988875,531358,1988548,1990005,1988157,740668,1988428,526782,1988359,1991183,1991873,1988114,1988790,1990200,1988357,1988772,1988238,1988438,1988121,1991247,1990215,1988208,1988502,1988516,1988779,1988664,1988782,1988433,1624557,1988788,1991202,1624539,1988590,1624540,1988592,1988803,1990828,548441,1988621,1989121,1988416,1988617,1988810,1820047,1988427,1988532&_callback=window.__termsJindo_callback._3551' -H 'Accept-Encoding: gzip,deflate,sdch' -H 'Accept-Language: en-US,en;q=0.8,ko;q=0.6,ja;q=0.4' -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.111 Safari/537.36' -H 'Accept: */*' -H 'Referer: http://m.terms.naver.com/list.nhn?cid=44630&categoryId=44630&so=st1.dsc' -H 'Cookie: NNB=HJBA4BIY3AYFI; npic=rg2GXF3Jh4UDlCpiOCAU28fYBwINA+cspv5/Rv2PrxBIyzTSWceHULsNZ1k0H24DCA==; WMONID=PORVV_ZRVH1; BMR=; page_uid=FmlzZg33w58ssakHiUnMussssuS-249380' -H 'Connection: keep-alive' --compressed
  val url = "http://m.terms.naver.com/listAjax.nhn"
  def param(categoryId:Int=44630,so:String="st3.asc", index:String="", page:Int, viewType:String="list")={

    Map("categoryId"->s"$categoryId",
    "so" -> s"$so",
    "page" -> s"$page",
    "viewType" -> s"$viewType")
  }
}
