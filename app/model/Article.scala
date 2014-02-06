package model

import reactivemongo.bson._
import reactivemongo.bson.BSONBoolean
import reactivemongo.bson.BSONLong
import reactivemongo.bson.BSONString
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.collections.default.BSONCollection
import scala.concurrent.Future
import scala.concurrent.{ExecutionContext}
import ExecutionContext.Implicits.global
import play.api.Play.current
import scala.util.{Failure, Success}


case class Article(_id: Option[BSONObjectID] = None, title: String, googleId: String, content: String, author: String, plusone: Long, shared: Long, current: Boolean, publicationDate: Long) {
  // def formatedPublicationDate(): String = publicationDate.dayOfMonth() + "/" + publicationDate.monthOfYear() + "/" + publicationDate.year()
  override def hashCode(): Int = (googleId + plusone + shared).hashCode

  override def equals(p1: scala.Any): Boolean = this.hashCode() == (p1.hashCode())

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
      Article(document.getAs[BSONObjectID]("_id"),
        document.getAs[BSONString]("title").getOrElse(BSONString("NO_VALUE")).value,
        document.getAs[BSONString]("googleId").getOrElse(BSONString("NO_VALUE")).value,
        document.getAs[BSONString]("content").getOrElse(BSONString("NO_VALUE")).value,
        document.getAs[BSONString]("author").getOrElse(BSONString("NO_VALUE")).value,
        document.getAs[BSONLong]("plusOne").getOrElse(BSONLong(-1)).value,
        document.getAs[BSONLong]("shared").getOrElse(BSONLong(-1)).value,
        document.getAs[BSONBoolean]("current").getOrElse(BSONBoolean(false)).value,
        document.getAs[BSONLong]("publicationDate").getOrElse(BSONLong(-1)).value
      )
    }
  }


  implicit object ArticleBSONWriter extends BSONDocumentWriter[Article] {
    def write(article: Article): BSONDocument =
      BSONDocument(
        "_id" -> article._id.getOrElse(BSONObjectID.generate),
        "title" -> BSONString(article.title),
        "googleId" -> BSONString(article.googleId),
        "content" -> BSONString(article.content),
        "author" -> BSONString(article.author),
        "plusOne" -> BSONLong(article.plusone),
        "shared" -> BSONLong(article.shared),
        "current" -> BSONBoolean(article.current),
        "publicationDate" -> BSONLong(article.publicationDate)
      )


  }

  def loadArticles(): Future[List[Article]] = {

    val query = BSONDocument("current" -> true)
    queryArticles(query)
  }

  def loadActiveArticleByGoogleId(googleId: String): Future[List[Article]] = {
    val query = BSONDocument("current" -> true, "googleId" -> googleId)
    queryArticles(query)

  }

  def unactiveArticle(a: Article) = {
    val articles = ReactiveMongoPlugin.db.collection[BSONCollection]("Article")

    val modifier = BSONDocument("$set" -> BSONDocument("current" -> false))

    articles.update(BSONDocument("_id" -> a._id), modifier).onComplete {
      case Failure(e) => throw e
      case Success(_) => println("successful!")
    }

  }

  def saveArticle(article: Article) {
    val articles = ReactiveMongoPlugin.db.collection[BSONCollection]("Article")
    articles.save(article)
  }

  private def queryArticles(query: BSONDocument): Future[List[Article]] = {

    val articles = ReactiveMongoPlugin.db.collection[BSONCollection]("Article")
    val cursor = articles.find(query).cursor[Article]
    cursor.collect[List]()

  }


}