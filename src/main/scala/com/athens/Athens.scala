package com.athens

import java.io.{File, FileOutputStream, PrintWriter}
import java.nio.file.Files

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.util.parsing.json.JSON

object Athens extends App {

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
    //    computeDataset("ParisSearchJan")
//    computeDataset("ParisSearchFeb")
    //    computeDataset("Oscars")
  }

  def computeDataset(dataset: String) = {
    current = dataset
    append = false
    val files: Seq[File] = new File(root + dataset).listFiles()
    println("Starting ...")
    files.foreach(println)
    files.foreach(compute)
  }

  def compute(first: File): Unit = {
    println("Reading freom file: " + first.getName)
    val raw = load(first)
    println("Read " + raw.size + " lines ... Parsing json ...")
    val start = System.currentTimeMillis
    val allTweets = raw.map(fromJson).filter(_.nonEmpty).map(_.get)
    println("Loaded json in " + (System.currentTimeMillis - start) + " ms")
    val uniqueTweets = allTweets.map(t => (t.hashtags, t)).toMap.values
    uniqueTweets.foreach(println)
    println("Total number of tweets: " + allTweets.size + " - Unique tweets: " + uniqueTweets.size)
    val permutations = uniqueTweets.flatMap(_.permutations).groupBy(t => t).mapValues(t => (t.size, t.map(_.id).toSet.mkString(","), t.map(_.uid).toSet.mkString(",")))
    store(permutations)
  }

  def load(file: File): Seq[String] = {
    val lines = Files.readAllLines(file.toPath).asScala
    lines.toSeq
  }

  def store(seq: Map[Permutation, (Int, String, String)]) = {
    println("Writing hashtag pairs to file ...")
    val f = new File(current + ".txt")
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
    val n: List[(String, List[Any], String)] = for {
      Some(M(map)) <- List(JSON.parseFull(json))
      // ID of tweet
      S(id) = map("id_str")
      // User of tweet
      M(userinfo) = map("user")
      S(name) = userinfo.get("id_str").get
      // get the hashtags
      M(entities) = map("entities")
      L(tags) = entities("hashtags")
      if (tags.size >= 2)
    } yield {
      (id, tags, name)
    }
    val hashtags = n.map(t => (t._1, t._2 match {
      case (a: List[Map[String, String]]) => a.map(x => x("text").toLowerCase)
      case _ => Nil
    }, t._3))
    // RETURN THE ACTUAL TWEET, IN THE TAGS
    if (hashtags.nonEmpty) Some(Tweet(id = hashtags(0)._1, hashtags = hashtags(0)._2.toSet, uid = hashtags(0)._3))
    else None
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
