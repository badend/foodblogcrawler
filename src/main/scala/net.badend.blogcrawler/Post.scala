package net.badend.blogcrawler

/**
 * Created by badend on 11/11/14.
 */
class BlogPost(val url:String,
               val title:String,
               val category:String,
               val date:String,
               val ingredient:String, val text:String, val images:Seq[String], val nickname:String, val id:String, val comment_no:Int, val like:Int) {

}
