package controllers

import play.api._
import play.api.mvc._
import _root_.utils.{HtmlUtils, GooglePlusAPIHelper}
import reactivemongo.bson._
import model.Article
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.collections.default.BSONCollection
import play.api.Play.current
import scala.concurrent.{ExecutionContext}
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
import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTime
import views.html.saved
import org.jsoup.Jsoup
import job.SynchroItem


object Application extends Controller {

  def articleFromDB = Action.async {

    val farticles = Article.loadArticles()

    farticles.map(art => Ok(views.html.allArticles(art.map(a => a.googleId + " " + a.title + " "+a.plusone + " " + a.shared ))))


  }

  def index = Action {
    val ArticlesFromGoogle = GooglePlusAPIHelper.getArticles()




    //Ok(views.html.showGoogleContent(articles.map(a => (HtmlUtils.extractTags(a.getObject.getContent)).mkString(","))))
    Ok(views.html.showGoogleContent(ArticlesFromGoogle.map(a => (a.googleId + " " + a.title + " "+a.plusone + " " + a.shared))))
  }

  def index2 = Action {
    println("start synchro")
    SynchroItem.synchroArticle()
    println("after synchro call")
    Ok(Json.toJson(List[String]("Hello World", "toto")))
  }

  def saveArticle = Action {
    val article = Article("test", "123", "content", "author", 12, 21, true, 123)

    import scala.concurrent.{ExecutionContext}
    import ExecutionContext.Implicits.global
    import play.api.Play.current
    // Gets a reference to the collection "acoll"
    // By default, you get a BSONCollection.
    val collection = ReactiveMongoPlugin.db.collection[BSONCollection]("Article")

    collection.save(article)


    Ok(saved())
  }


  def displayArticles = Action.async {


    implicit object articleJsonWrite extends Writes[Article] {
      def writes(o: Article): JsValue = JsArray(Seq(JsString(o.title),
        //"googleId" -> o.googleId,
        //"content" -> o.content,
        JsString(o.author),
        JsString("" + o.plusone),
        JsString("" + o.shared),
        JsString("" + o.publicationDate)
        //"current" -> o.current
      ))
    }


    Article.loadArticles().map(articles => Ok(JsObject(Seq(("aaData", Json.toJson(articles))))))
  }

}