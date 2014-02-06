/**
 * Created by fsznajderman on 16/01/2014.
 */

import job.Synchro.{Tags, Articles}
import play.api._
import play.api.libs.concurrent.Akka
import akka.actor.Props
import job.Synchro
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global

object Global extends GlobalSettings {
  override def onStart(app: Application): Unit = {
    import play.api.Play.current
    Logger.logger.info("Start application")
    val synchroArticles = Akka.system(app).actorOf(Props[Articles], name = "synchroArticles")
    Akka.system.scheduler.schedule(0.seconds, 30.minutes, synchroArticles, "go")
    val synchroTags = Akka.system(app).actorOf(Props[Tags], name = "synchroTags")
    Akka.system.scheduler.schedule(10.seconds, 30.minutes, synchroTags, "go")
  }
}