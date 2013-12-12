package job

import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.collections.default.BSONCollection
import model.Article
import utils.GooglePlusAPIHelper
import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Failure, Success}

/**
 * Created by fsznajderman on 10/12/2013.
 */
object SynchroItem {
import ExecutionContext.Implicits.global

def synchroArticle() {

    Future {
      println("in Future call")
      GooglePlusAPIHelper.getArticles

    } onComplete {
      case Failure(t) => throw t
      case Success(articleFromGoogle) => {
        Article.loadArticles onComplete {
          case Failure(t) => throw t
          case Success(as) => articleFromGoogle.diff(as) match {
            case Nil =>  println("nothing found : diff")
            case all => println(all.map(d => d.title  ))
          }
        }
      }
    }
  }

}
