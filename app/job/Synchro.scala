package job

import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.collections.default.BSONCollection
import model.{Tag, Article}
import utils.{HtmlUtils, GooglePlusAPIHelper}
import scala.concurrent.{Future, ExecutionContext}
import akka.actor.Actor
import play.Logger
import ExecutionContext.Implicits.global
import akka.actor.Status.{Failure, Success}

/**
 * Created by fsznajderman on 10/12/2013.
 */
object Synchro {

  class Articles extends Actor {
    def receive = {
      case _ =>
        Logger.info("Synchro Articles launched")
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
              case a => a.foreach(f => Article.unactiveArticle(f)); Article.saveArticle(current)
            })
            // For each article : find the current article
            // update old one to false
            // and create new one with the one that has been found with diff function


          }
    }
  }

  class Tags extends Actor {
    override def receive: Actor.Receive = {
      case _ =>
        Logger.info("Synchro Tags launched")
        Article.loadArticles().onSuccess {
          case Nil => println("no articles found")
          case articles => {

            val tags = articles.map(article => HtmlUtils.extractTags(article.content)
              .map(sTag => Tag(None, sTag, List(article.googleId))))
              .flatten.groupBy(tag => tag.lbl).mapValues(list => list.map(t => t.articleIds(0)))
            Tag.cleanTagsCollection().onSuccess {
              case _ => tags.foreach(par => Tag.saveTag(Tag(None, par._1, par._2)))
            }


          }
        }


    }


  }

}
