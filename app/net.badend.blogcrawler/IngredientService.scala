package net.badend.blogcrawler

import java.nio.charset.Charset

/**
 * Created by badend on 11/30/14.
 */
object IngredientService {

  final val HANGUL_UNICODE_START = 0xAC00
  final val HANGUL_UNICODE_END = 0xD7AF
  val ingredient = if(System.getProperty("ingredient") != null && System.getProperty("ingredient").size>1) {
    System.getProperty("ingredient")
  }else{
    s"${System.getProperty("user.dir")}/data/ingredient3"
  }
  val ingredients = scala.io.Source.fromFile(ingredient)(Charset.forName("UTF8")).getLines().zipWithIndex
  val ac = AhoCorasickBuilder.apply(ingredients.map(x=> ACData(x._1, x._2)).toSeq).build()


}
