package controllers

import play.api.mvc._
import model.Tag
import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsArray
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

/**
 * Created by fsznajderman on 23/01/2014.
 */
object TestControler extends Controller {

  implicit object tagJsonWrite extends Writes[Tag] {
    def writes(o: Tag): JsValue = JsArray(Seq(JsString(o.lbl),Json.toJson(o.articleIds)
      //"current" -> o.current
    ))
  }

  def testTag = Action.async {
    Tag.addArticleToTag("4ff0b892e4b0c979b4b842fc", "").map(oTag => Ok(JsObject(Seq(("aaData", Json.toJson(oTag))))))
  }


}


