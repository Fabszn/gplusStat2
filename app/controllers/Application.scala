package controllers

import play.api.mvc._
import _root_.utils.{GooglePlusAPIHelper}
import model.Article
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.api.Play.current
import scala.concurrent.{ExecutionContext}
import ExecutionContext.Implicits.global
import play.api.libs.json._
import play.api.libs.json.JsArray
import reactivemongo.api.collections.default.BSONCollection
import views.html.saved
import job.SynchroItemActor


object Application extends Controller {

  def index = Action {

    Ok(views.html.index())
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

  /*def articleFromDB = Action.async {

    val farticles = Article.loadArticles()

    farticles.map(art => Ok(views.html.allArticles(art.map(a => a.googleId + " " + a.title + " " + a.plusone + " " + a.shared))))


  }*/


  /*def saveArticle = Action {
    val article = Article(None,"test", "123", "content", "author", 12, 21, true, 123)

    import scala.concurrent.{ExecutionContext}
    import ExecutionContext.Implicits.global
    import play.api.Play.current
    // Gets a reference to the collection "acoll"
    // By default, you get a BSONCollection.
    val collection = ReactiveMongoPlugin.db.collection[BSONCollection]("Article")

    collection.save(article)


    Ok(saved())
  }*/

  /*def updateArticle = Action {
    Article.loadActiveArticleByGoogleId("z123yhuzopy3udluv23jencjjn2fsrmpu04") onSuccess {
      case a => a.foreach(f => Article.updateArticle(f))
    }
    Ok("ok")
  }*/



}