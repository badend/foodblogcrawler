package net.badend.blogcrawler

import scala.collection.mutable.{ArrayBuffer, LinkedList, Queue => MQueue}

case class Match[T](start: Int, target: String, actual: String, data: T)

class AhoCorasickBuilder[T](charMap: Char => Char = _.toLower) {
  private var rootGoto: Goto = new Goto with RootGoto

  private trait RootGoto {
    self: Goto =>
    override def goto(c: Char): Option[Goto] = next.get(c).map(_.data) orElse (Some(self))
  }

  private class Goto {
    val next: Node[Goto] = Node(this)
    var outputs: Option[LinkedList[ACData[T]]] = None
    var fail: Option[Goto] = None
    def goto(c: Char): Option[Goto] = next.get(c).map(_.data)
    def failToString: String = {
      fail.fold("<>")(s => "<" + s.toString + ">")
    }
    override def toString: String = {
      "goto(%d,%s,%s,%s)".format(outputs, failToString, next.entries.map(_.map(neToString(_)).mkString("[", ",", "]")))
    }
  }

  /**
   * The read-only Aho-Corasick finder
   */
  class AhoCorasick(rootGoto: Goto) {
    def find(in: String): Seq[Match[T]] = {
      var state = rootGoto
      val builder = Vector.newBuilder[Match[T]]
      in.map(charMap(_)).zipWithIndex.foreach {
        case (c, i) => {
          while (!state.goto(c).isDefined) { state = state.fail.get }
          state = state.goto(c).get
          state.outputs.foreach {
            s =>
            {
              builder ++= s.toSeq.map(x => Match(i - x.string.length + 1, x.string, in.slice(i - x.string.length + 1, i + 1), x.data))
            }
          }
        }
      }
      builder.result
    }
  }

  /**
   * Add data to the builder
   *
   * @param in value of type `Data[T]` to add to the trie.
   * @return the builder
   */
  def +=(in: ACData[T]): AhoCorasickBuilder[T] = {
    if (!in.string.isEmpty) {
      val target = in.string.map(charMap(_)).foldLeft(rootGoto) {
        (g, c) =>
        {
          g.next.get(c).fold({
            val n = (new Goto).next
            g.next += c -> n
            n.data
          })(s => s.data)
        }
      }
      target.outputs.fold(target.outputs = Some(LinkedList(in)))(s => s.+:(in))
    }
    this
  }

  def neToString(ne: NodeEntry[Goto]): String =
    "{%s: %s}".format(ne.char, ne.node.data.toString)

  /**
   * Build the Aho-Corasick finder instance.
   */
  def build(): AhoCorasick = {
    val queue = MQueue[Goto]()
    rootGoto.next.entries.foreach {
      _.foreach {
        (s: NodeEntry[Goto]) =>
        {
          s.node.data.fail = Some(rootGoto)
          queue += s.node.data
        }
      }
    }
    while (!queue.isEmpty) {
      val r = queue.dequeue()
      r.next.entries.foreach {
        _.foreach {
          (xx: NodeEntry[Goto]) =>
          {
            val a: Char = xx.char
            val s: Goto = xx.node.data
            queue += s
            var state = r.fail.get
            while (!state.goto(a).isDefined) {
              state = state.fail.get
            }
            val down = state.goto(a).get
            s.fail = Some(down)
            down.outputs.foreach {
              dos =>
              {
                s.outputs.fold(s.outputs = Some(dos))(s1 => s.outputs = Some(s1.++:(dos)))
              }
            }
          }
        }
      }
    }
    val result = new AhoCorasick(rootGoto)
    rootGoto = new Goto with RootGoto
    result
  }
}

case class NodeEntry[T](char: Char, node: Node[T])

case class Node[T](data: T, threshold: Int = 6) {
  var entries: Option[ArrayBuffer[NodeEntry[T]]] = None
  def +=(n: (Char, Node[T])): Node[T] = {
    insert(NodeEntry(n._1, n._2))
    this
  }
  def get(char: Char): Option[Node[T]] = {
    entries.flatMap {
      ab =>
      {
        ab.length match {
          case 0 => None
          case 1 => if (char == ab(0).char) Some(ab(0).node) else None
          case n => {
            if (char < ab(0).char || char > ab.last.char) None
            else if (n < threshold) {
              var i: Int = 0
              while (i < n && char != ab(i).char) i += 1
              if (i < n && ab(i).char == char) Some(ab(i).node) else None
            } else {
              var t = n
              var b = 0
              var p = t / 2
              while (p < n && char != ab(p).char && b < t && p < t) {
                if (char < ab(p).char) t = p
                else b = p
                val tb2 = (t + b) / 2
                p = if (p == tb2) tb2 + 1 else tb2
              }
              if (char == ab(p).char) Some(ab(p).node)
              else None
            }
          }
        }
      }
    }
  }
  def insert(node: NodeEntry[T]): Node[T] = {
    val char = node.char
    def findInsertIndex(ab: ArrayBuffer[NodeEntry[T]]): Int = {
      ab.length match {
        case 0 => 0
        case 1 =>
          if (char < ab(0).char) 0
          else 1
        case n => {
          if (char < ab(0).char) 0
          else if (char > ab.last.char) ab.length
          else if (n < threshold) {
            var i: Int = 0
            while (i < n && char >= ab(i).char) i += 1
            i
          } else {
            var t = n
            var b = 0
            var p = t / 2
            while (p < n && char != ab(p).char && p < t) {
              if (char < ab(p).char) t = p
              else b = p
              val tb2 = (t + b) / 2
              p = if (p == tb2) tb2 + 1 else tb2
            }
            p
          }
        }
      }
    }
    def doInsert(ab: ArrayBuffer[NodeEntry[T]]): ArrayBuffer[NodeEntry[T]] = {
      ab.insert(findInsertIndex(ab), node)
      ab
    }
    entries.fold({
      entries = Some(ArrayBuffer(node))
      this
    })(
        ab => {
          doInsert(ab)
          this
        })
  }
}


case class ACData[T](string: String, data: T)/**
 * Companion object for AhoCorasickBuilder
 */
object AhoCorasickBuilder extends App {
  /**
   * Represents data stored with a dictionary entry
   */
  implicit def toData[T](x: (String, T)): ACData[T] = ACData(x._1, x._2)
  def apply[T](in: Seq[ACData[T]], charMap: Char => Char = _.toLower): AhoCorasickBuilder[T] =
    in.foldLeft(new AhoCorasickBuilder[T](charMap))((s, v) => s += v)

  override def main(args: Array[String]) = {

    val data = ""
    val title = ""
    val author = ""

    //val titleAC = AhoCorasickBuilder(title).build
    //val authorAC = AhoCorasickBuilder(author).build

    //println(titleAC.find("'국민언니' 김경호, 10번의 좌절과 영광 '국민언니' 김경호, 10번의 좌절과 영광"))
  }
}