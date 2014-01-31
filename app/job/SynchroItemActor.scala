package job

import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.collections.default.BSONCollection
import model.Article
import utils.GooglePlusAPIHelper
import scala.concurrent.{Future, ExecutionContext}
import akka.actor.Actor
import play.Logger

/**
 * Created by fsznajderman on 10/12/2013.
 */
class SynchroItemActor extends Actor {

  import ExecutionContext.Implicits.global

  def receive = {
    case _ =>
      Logger.info("Actor launched")
      val googleArticles = Future {
        GooglePlusAPIHelper.getArticles
      }

      for {
        resultDB <- Article.loadArticles
        resultGoogle <- googleArticles
      } yield
        resultGoogle.diff(resultDB) match {
          case Nil => println("nothing found : diff")
          case all => all.foreach(current => Article.loadActiveArticleByGoogleId(current.googleId) onSuccess {
            case a => a.foreach(f => Article.updateArticle(f)); Article.saveArticle(current)
          })
          // For each article : find the current article
          // update old one to false
          // and create new one with the one that has been found with diff function


        }
  }
}
