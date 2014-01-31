package model

import org.specs2.Specification
import org.scalatest.FlatSpec
import reactivemongo.bson.BSONObjectID

/**
 * Created by fsznajderman on 11/12/2013.
 */

class ArticleTest extends FlatSpec {


  "a1 and a2" must "be equals" in {

    val a1 = Article(Option(BSONObjectID("12")),"Title", "ABC", "content", "me", 12, 12, false, 123)
    val a2 = Article(Option(BSONObjectID("12")),"Title", "ABC", "content", "me", 12, 12, false, 123)
    assert(a1.equals(a2))
  }

  it must "be equals with same value for :googleid, plusone, shared" in {
    val a1 = Article(Option(BSONObjectID("12")),"Title1", "ABC", "content", "me", 12, 12, false, 123)
    val a2 = Article(Option(BSONObjectID("12")),"Title", "ABC", "content", "me", 12, 12, false, 123)
    assert(a1.equals(a2))
  }

  it must "be not equals when plusone is not the same" in {

    val a1 = Article(Option(BSONObjectID("12")),"Title", "ABC", "content", "me", 11, 12, false, 123)
    val a2 = Article(Option(BSONObjectID("12")),"Title", "ABC", "content", "me", 12, 12, false, 123)
    assert(!a1.equals(a2))
  }

  it must "be not equals when googleId is not the same" in {

    val a1 = Article(Option(BSONObjectID("12")),"Title", "ABCD", "content", "me", 12, 12, false, 123)
    val a2 = Article(Option(BSONObjectID("12")),"Title", "ABC", "content", "me", 12, 12, false, 123)
    assert(!a1.equals(a2))
  }

  it must "be not equals when shared is not the same" in {

    val a1 = Article(None,"Title", "ABC", "content", "me", 12, 13, false, 123)
    val a2 = Article(None,"Title", "ABC", "content", "me", 12, 12, false, 123)
    assert(!a1.equals(a2))
  }

  "Article 3 " should "contained in list result" in {
    val a1 = Article(None,"Title", "ABC", "content", "me", 12, 13, false, 123)
    val a2 = Article(Option(BSONObjectID("12")),"Title", "ABC", "content", "me", 12, 13, false, 123)
    val a3 = Article(Option(BSONObjectID("12")),"Title", "ABCD", "content", "me", 12, 13, false, 123)
    val l1 = List(a1,a2,a3)
    val l2 = List(a1,a2)

    val result = l1.diff(l2)
    assert(result.contains(a3))


  }

}
