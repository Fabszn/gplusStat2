package controllers

import play.api._
import play.api.mvc._
import utils.{GooglePlusAPIHelper}
import reactivemongo.bson._
import model.Article
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.collections.default.BSONCollection
import play.api.Play.current
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import play.api.libs.json._
import reactivemongo.bson.BSONString
import reactivemongo.api.collections.default.BSONCollection
import scala.util.parsing.json.JSONArray
import reactivemongo.bson.BSONLong
import play.api.libs.json.JsArray
import reactivemongo.bson.BSONBoolean
import reactivemongo.bson.BSONString
import reactivemongo.api.collections.default.BSONCollection


object Application extends Controller {

  def index = Action {
    Ok(views.html.index(""))
  }

  def index2 = Action {
    Ok(Json.toJson(List[String]("Hello World", "toto")))
  }


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
        document.getAs[BSONBoolean]("current").get.value)
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
        "plusone" -> BSONLong(article.plusone),
        "shared" -> BSONLong(article.shared),
        "current" -> BSONBoolean(article.current)
      )

    /*if (article.creationDate.isDefined)
      bson += "creationDate" -> BSONDateTime(article.creationDate.get.getMillis)
    if (article.updateDate.isDefined)
      bson += "updateDate" -> BSONDateTime(article.updateDate.get.getMillis)*/


  }



  def displayArticles = Action.async {

    implicit object articleJsonWrite extends Writes[Article] {
      def writes(o: Article): JsValue = JsArray(Seq(JsString(o.title),
        //"googleId" -> o.googleId,
        //"content" -> o.content,
        JsString(o.author),
        JsString(""+o.plusone),
        JsString(""+o.shared)
        //"current" -> o.current
      ))
    }

    // Gets a reference to the collection "acoll"
    // By default, you get a BSONCollection.
    val collection = ReactiveMongoPlugin.db.collection[BSONCollection]("Article")
    val query = BSONDocument("current" -> true)

    val cursor = collection.find(query).cursor[Article]

    val futureList = cursor.collect[List](100)
    //OK(Json.toJson(Article("","","","",12l,12l,true)))
    futureList.map(articles => Ok(JsObject(Seq(("aaData",Json.toJson(articles))))))
  }

}