package model

import play.api.libs.json.{JsValue, Writes}
import play.api.libs.json._
import java.util.Date
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import reactivemongo.bson._
import reactivemongo.bson.BSONBoolean
import reactivemongo.bson.BSONLong
import reactivemongo.bson.BSONString
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.collections.default.BSONCollection
import scala.concurrent.Future


case class Article(title: String, googleId: String, content: String, author: String, plusone: Long, shared: Long, current: Boolean, publicationDate: Long) {
  // def formatedPublicationDate(): String = publicationDate.dayOfMonth() + "/" + publicationDate.monthOfYear() + "/" + publicationDate.year()
  override def hashCode(): Int = (googleId + plusone + shared).hashCode

  override def equals(p1: scala.Any): Boolean = this.hashCode().equals(p1.hashCode())
}


/*
* private String title;
    private String googleId;
    private String content;
    private String author;
    private long publicationDate;
    private long insertionDate;
    private long plusOne;
    private long shared;
    private boolean current = false;
    private String url;
* */


object Article {

  implicit object ArticleBSONReader extends BSONDocumentReader[Article] {
    def read(document: BSONDocument): Article = {
      //al doc = document.toTraversable
      Article(
        document.getAs[BSONString]("title").get.value,
        document.getAs[BSONString]("googleId").get.value,
        document.getAs[BSONString]("content").get.value,
        document.getAs[BSONString]("author").get.value,
        document.getAs[BSONLong]("plusOne").get.value,
        document.getAs[BSONLong]("shared").get.value,
        document.getAs[BSONBoolean]("current").get.value,
        document.getAs[BSONLong]("publicationDate").get.value
      )
    }
  }

  implicit object ArticleBSONWriter extends BSONDocumentWriter[Article] {
    def write(article: Article): BSONDocument =
      BSONDocument(
        //"_id" -> article.id.getOrElse(BSONObjectID.generate),
        "title" -> BSONString(article.title),
        "googleId" -> BSONString(article.googleId),
        "content" -> BSONString(article.content),
        "author" -> BSONString(article.author),
        "plusOne" -> BSONLong(article.plusone),
        "shared" -> BSONLong(article.shared),
        "current" -> BSONBoolean(article.current),
        "publicationDate" -> BSONLong(article.publicationDate)
      )

    /*if (article.creationDate.isDefined)
      bson += "creationDate" -> BSONDateTime(article.creationDate.get.getMillis)
    if (article.updateDate.isDefined)
      bson += "updateDate" -> BSONDateTime(article.updateDate.get.getMillis)*/


  }

  def loadArticles(): Future[List[Article]] = {
    import scala.concurrent.{ExecutionContext}
    import ExecutionContext.Implicits.global
    import play.api.Play.current
    println("articles from DB")

    // Gets a reference to the collection "acoll"
    // By default, you get a BSONCollection.
    val collection = ReactiveMongoPlugin.db.collection[BSONCollection]("Article")
    val query = BSONDocument("current" -> true)

    val cursor = collection.find(query).cursor[Article]
    println("articles from DB 2")
    cursor.collect[List](1000)

  }


}