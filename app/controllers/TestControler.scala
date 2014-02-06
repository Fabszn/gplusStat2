package controllers

import model.Tag
import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsArray
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import views.html.saved
import play.api.mvc.{Action, Controller}

/**
 * Created by fsznajderman on 23/01/2014.
 */
object TestControler extends Controller {

  implicit object tagJsonWrite extends Writes[Tag] {
    def writes(o: Tag): JsValue = JsArray(Seq(JsString(o.lbl), Json.toJson(o.articleIds)
      //"current" -> o.current
    ))
  }

  def testTag = Action {

    Tag.cleanTagsCollection()
    Ok("OK")

  }


}


