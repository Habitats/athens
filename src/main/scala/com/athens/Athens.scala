package com.athens

import java.io.{File, FileOutputStream, PrintWriter}
import java.nio.file.Files

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

object Athens extends App {
  implicit val formats = Serialization.formats(NoTypeHints)

  var append = false
  var current = ""
  lazy val root = "C:\\Archive\\athensDataTwitter\\"

  class CC[T] {
    def unapply(a: Any): Option[T] = Some(a.asInstanceOf[T])
  }

  object M extends CC[Map[String, Any]]

  object L extends CC[List[Any]]

  object S extends CC[String]

  object D extends CC[Double]

  object B extends CC[Boolean]


  override def main(args: Array[String]) = {
    computeDataset("NewYorkOneWeek")
    computeDataset("ParisSearchJan")
    computeDataset("ParisSearchFeb")
    computeDataset("Oscars")
  }

  def computeDataset(dataset: String) = {
    val start = System.currentTimeMillis
    current = dataset
    append = false
    val files: Seq[File] = new File(root + dataset).listFiles()
    println("Starting ...")
    files.foreach(println)
    val tweets = files.flatMap(compute)
    println(s"Loading completed in ${((System.currentTimeMillis - start) / 1000)} seconds!")

    val uniqueTweets = tweets.map(t => (t.hashtags, t)).toMap.values
//    uniqueTweets.foreach(println)
    println("Total number of tweets: " + tweets.size + " - Unique tweets: " + uniqueTweets.size)
    val permutations = uniqueTweets.flatMap(_.permutations).groupBy(t => t).mapValues(t => (t.size, t.map(_.id).toSet.mkString(","), t.map(_.uid).toSet.mkString(",")))
    store(permutations)
  }

  def compute(first: File): Seq[Tweet] = {
    println("Reading freom file: " + first.getName)
    val raw = load(first)
    println("Read " + raw.size + " lines ... Parsing json ...")
    val start = System.currentTimeMillis
    val allTweets = raw.map(fromJson).filter(_.nonEmpty).map(_.get)
    println("Loaded json in " + (System.currentTimeMillis - start) + " ms")
    allTweets
  }

  def load(file: File): Seq[String] = {
    val lines = Files.readAllLines(file.toPath).asScala
    lines.toSeq
  }

  def store(seq: Map[Permutation, (Int, String, String)]) = {
    println("Writing hashtag pairs to file ...")
    val f = new File(current)
    if (!append) {
      if (f.exists) {
        f.delete
      }
      f.createNewFile
      append = true
    }
    val writer = new PrintWriter(new FileOutputStream(f, append))
    seq.map(p => s"${p._1.tag1} ${p._1.tag2} ${p._2._1} ${p._2._2} ${p._2._3}").foreach(writer.println)
    writer.close
    println("Writing complete!")
  }

  def fromJson(json: String): Option[Tweet] = {
    val full = parse(json)
    val n: (String, String) = (for {
      JObject(child) <- full
      JField("id_str", JString(id)) <- child
      JField("user", JObject(userinfo)) <- child
      JField("id_str", JString(uid)) <- userinfo
    } yield (id, uid)) (0)

    val m: List[String] = for {
      JObject(child) <- full
      JField("entities", JObject(entities)) <- child
      JField("hashtags", JArray(hashtags)) <- entities
      JObject(tag) <- hashtags
      JField("text", JString(text)) <- tag
    } yield {
      text
    }

    if (m.size < 2) None
    else Some(Tweet(id = n._1, hashtags = m.toSet, uid = n._2))
  }
}

case class Permutation(tag1: String, tag2: String, id: String, uid: String) {
  lazy val unique = Seq(tag1, tag2).sorted.mkString(":")

  override def toString = s"$tag1 $tag2 $id $uid"

  override def equals(o: Any) = o match {
    case that: Permutation => that.unique.equals(this.unique)
    case _ => false
  }

  override def hashCode = unique.hashCode
}

case class Tweet(id: String, hashtags: Set[String], uid: String) {

  override def toString = s"$id - tags: ${hashtags.mkString(", ")}"

  lazy val permutations = {
    val tagPairs = new ListBuffer[Set[String]]()
    for (t1 <- hashtags) {
      for (t2 <- hashtags) {
        if (t1 != t2) {
          val tuple = Set(t1, t2)
          tagPairs += tuple
        }
      }
    }
    tagPairs.map(_.toSeq).map(p => Permutation(p(0), p(1), id, uid))
  }

  def printTags = {
    println("\t" + id)
    permutations.foreach(p => println("\t\t" + p))
  }
}
