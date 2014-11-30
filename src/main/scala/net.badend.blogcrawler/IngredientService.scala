package net.badend.blogcrawler

/**
 * Created by badend on 11/30/14.
 */
object IngredientService {

  final val HANGUL_UNICODE_START = 0xAC00
  final val HANGUL_UNICODE_END = 0xD7AF
  val ingredients = scala.io.Source.fromFile("data/ingredient3").getLines().zipWithIndex
  val ac = AhoCorasickBuilder.apply(ingredients.map(x=> ACData(x._1, x._2)).toSeq).build()


}
