package net.badend.blogcrawler


import java.nio.charset.Charset
import java.nio.file.{Paths, Files}

import org.json4s._
import org.json4s.native.JsonMethods._
import spray.http.DateTime

/**
 * Created by badend on 11/15/14.
 */
object IngredientMaker {
  def main(args:Array[String])={
  miznetProvider(args(0))
  }

  val unit = Set("개", "t", "tc", "gm", "그람", "그램", "숫갈", "ts", "수저", "수푼", "스푼", "약간", "cc", "컵", "ml", "적당", "적당량",
  "적절히", "적당양", "적절", "조금", "많이", "개반", "뿌리", "장", "작은술", "큰술", "모", "근", "반근", "되", "줌", "c", "반줌", "반되", "소", "토막"
  ,"티스푼", "스푼 반", "덩이", "팩", "숫가락", "포기" , "살짝", "반개", "단", "쪽", "방울", "숟가락", "잎", "g", "g이상").zipWithIndex
  val trieBuilfer = new AhoCorasickBuilder[(Int)]

  unit.foreach(x=>trieBuilfer += ACData(x._1.reverse, (x._2)))

  val trie = trieBuilfer.build()

  def miznetProvider(file:String) = {

    val fw = Files.newBufferedWriter(Paths.get(s"${System.getProperty("user.dir")}/data/ingredient.${DateTime.now.toIsoDateString}"), Charset.forName("UTF8"))

    //val miznet = "data/miznetcookDataS.2014-11-15"
    val mets = scala.io.Source.fromFile(file, "utf8").getLines.toSeq.flatten{
      line =>
        val parsed = parse(line)
        val met1 = parsed \ "met1"
        val met2 = parsed \ "met2"



        var total =met1.toString.trim.split("[,:\t-]", -1).map{
          word =>
            val words = word.split("[\\s]+")
            words.map(x=>x.trim).filter{
              w=>
               val idx = trie.find(w.reverse)

              idx.isEmpty

            }.mkString(" ")
        }

        total
    }.map(x=>x.trim).filter(x=>x.length>0).toSet
    fw.write(mets.mkString("\n").replaceAll("\\d+",""))

    fw.close()
  }

}
