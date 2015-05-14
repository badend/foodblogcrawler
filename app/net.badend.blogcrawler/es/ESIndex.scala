package net.badend.blogcrawler.es


import java.nio.charset.Charset
import java.sql.DriverManager

import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType
import com.sksamuel.elastic4s.mappings.FieldType.{StringType, IntegerType}
import com.sksamuel.elastic4s.source.StringDocumentSource
import org.apache.lucene.analysis.cjk.CJKAnalyzer
import org.elasticsearch.indices.IndexAlreadyExistsException
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{read => r, write => w}
import sun.misc.BASE64Encoder


/**
 * Created by jihoonkang on 3/18/15.
 */
object ESIndex {

  val cols = Map(
    ("rc.id" -> ("rc.id", "rc.id" typed IntegerType)),
    ("rc.recipes_id" -> ("rc.recipes_id", "rc.recipes_id" typed IntegerType)),
    ("rc.content"->("rc.content", "rc.content" typed StringType analyzer "cjk")),
    ("r.id"->("r.id", "r.id" typed IntegerType)),
    ("r.members_id"->("r.members_id", "r.members_id" typed IntegerType)),
    ("r.cookbook_id"->("r.cookbook_id", "r.cookbook_id" typed IntegerType)),
    ("r.cook_name"->("r.cook_name", "r.cook_name" typed StringType analyzer "cjk")),
    ("r.cook_hour"->("r.cook_hour", "r.cook_hour" typed IntegerType)),
    ("r.cook_minute"->("r.cook_minute", "r.cook_minute" typed IntegerType)),
    ("r.difficulty"->("r.difficulty", "r.difficulty" typed IntegerType)),
    ("r.quantity"->("r.quantity", "r.quantity" typed IntegerType)),
    ("r.stuff"->("r.stuff", "r.stuff" typed StringType analyzer "cjk")),
    ("r.reg_date"->("r.reg_date", "r.reg_date" typed StringType)),
    ("r.edit_date"->("r.edit_date" , "r.edit_date" typed StringType)),
    ("r.web_type"->("r.web_type", "r.web_type" typed StringType)),
    ("r.web_url"->("r.web_url", "r.web_url" typed StringType)),
    ("r.web_user_id"->("r.web_user_id", "r.web_user_id" typed StringType analyzer "cjk")),
    ("r.web_user_nickname"->("r.web_user_nickname", "r.web_user_nickname" typed StringType analyzer "cjk")))

  val col = cols.map(x=> x._2._1).toList

  def main(args:Array[String]) = {

    implicit val formats = org.json4s.DefaultFormats

    val uri = ElasticsearchClientUri("elasticsearch://gourmetmarket.co:3200")
    val client = ElasticClient.local
    //val client = ElasticClient.remote("54.64.64.193", 9300)

    client.execute{deleteIndex("hello")}
    println(client.admin.cluster.prepareClusterStats())

    println(w(client))
    val docs = DBCon.getRecipes
    println(s"docs size is ${docs.size}")


    println(client)

    try {
      println(client.execute {
        create index "hello" mappings {
          "recipes" as cols.map(x => x._2._2)
        }

      }.await)
    }catch{
      case e:IndexAlreadyExistsException => {
        e.printStackTrace()
      }
    }



    docs.map(d => client.execute {
      val base64 = new org.apache.commons.codec.binary.Base64

      val idCandidate = Option(d.getOrElse(
        "r.web_url",
        d("r.id")
      )).getOrElse(d("r.id")).getBytes(
          Charset.forName("UTF8"))
      println(idCandidate)
      val did=  base64.encode(idCandidate)
      val bejson = d + ("_id" -> did)
      index into "hello/recipe" doc StringDocumentSource(w(bejson))
    }.await
    )
    println(client.execute{
      search in "hello/recipe"
    }.await)

  }

}

object DBCon {
  def main(args: Array[String]) ={

  }
  def getRecipes = {
    val dbc = "jdbc:mysql://gourmetmarket.co:3306/hellodollymarket?user=hellodolly&password=hellodolly"
    classOf[com.mysql.jdbc.Driver]
    val conn = DriverManager.getConnection(dbc)
    // do database insert
    try {
      val prep = conn.prepareStatement(s"SELECT ${ESIndex.cols.keySet.mkString(",")} FROM recipe_contents rc,recipes r where rc.recipes_id=r.id")
      val r = prep.executeQuery()
      new Iterator[Map[String, String]] {
        def hasNext = r.next()

        def next() = ESIndex.cols.map(x => (x._2._1, r.getString(x._1))).toMap
      }.toList
    }
    finally {
      conn.close
    }
  }
}
