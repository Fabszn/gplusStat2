import model.Tag
import org.scalatest.FlatSpec
import play.api.libs.json._

/**
 * Created by fsznajderman on 01/02/2014.
 */
class JsonTest extends FlatSpec {

  implicit object tagJsonWrite extends Writes[Tag] {
    def writes(o: Tag): JsValue = JsObject(List((o.lbl, JsNumber(o.articleIds.size))))

  }

  "result " should "have format {'lb1':2,'lb2':2} " in {

    val t1 = Tag(None, "lb2", List("", ""))
    val t2 = Tag(None, "lb1", List("", ""))

    val tags = List(t1, t2);
    println(JsObject(tags.map(t => (t.lbl, JsNumber(t.articleIds.size))).toList))
    println("{\"lb2\":2,\"lb1\":2}")
    assert("{\"lb2\":2,\"lb1\":2}".equalsIgnoreCase((JsObject(tags.map(t => (t.lbl, JsNumber(t.articleIds.size))).toList).toString())))


  }

}
