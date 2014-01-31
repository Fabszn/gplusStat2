/**
 * Created by fsznajderman on 16/01/2014.
 */

import play.api._
import play.api.libs.concurrent.Akka
import akka.actor.Props
import job.SynchroItemActor
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global

object Global extends GlobalSettings {
  override def onStart(app: Application): Unit = {
    import play.api.Play.current
    Logger.logger.info("Start application Fabrice")
    val synchroActor = Akka.system(app).actorOf(Props[SynchroItemActor], name = "synchroActor")
    Akka.system.scheduler.schedule(0.seconds, 30.minutes,synchroActor,"go")
  }
}