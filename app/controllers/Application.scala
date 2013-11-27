package controllers

import play.api._
import play.api.mvc._
import utils.{GooglePlusAPIHelper}
import reactivemongo.bson.{BSONDocumentWriter, BSONString, BSONDocument, BSONDocumentReader}
import model.Article
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.collections.default.BSONCollection
import play.api.Play.current
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

object Application extends Controller {

  def index = Action {


    Ok(views.html.index("" + GooglePlusAPIHelper.getArticles().size))
  }


  implicit object ArticleBSONReader extends BSONDocumentReader[Article] {
    def read(document: BSONDocument): Article = {
      //al doc = document.toTraversable
      Article(
        document.getAs[BSONString]("title").get.value,
        document.getAs[BSONString]("googleId").get.value)
    }
  }

  implicit object ArticleBSONWriter extends BSONDocumentWriter[Article] {
    def write(article: Article): BSONDocument =
      BSONDocument(
        //"_id" -> article.id.getOrElse(BSONObjectID.generate),
        "title" -> BSONString(article.title),
        "googleId" -> BSONString(article.googleId)
      )

    /*if (article.creationDate.isDefined)
      bson += "creationDate" -> BSONDateTime(article.creationDate.get.getMillis)
    if (article.updateDate.isDefined)
      bson += "updateDate" -> BSONDateTime(article.updateDate.get.getMillis)*/


  }

  def displayArticles = Action.async {


    // Gets a reference to the collection "acoll"
    // By default, you get a BSONCollection.
    val collection = ReactiveMongoPlugin.db.collection[BSONCollection]("Article")
    val query = BSONDocument("current" -> true)

    val cursor = collection.find(query).cursor[Article]


    val futureList = cursor.collect[List](100)

    futureList.map(articles => Ok(views.html.allArticles(articles.map(a => a.toString))))
  }

}