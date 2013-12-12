package utils

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import model.Tag

/**
 * Created by fsznajderman on 07/12/2013.
 */
object HtmlUtils {


  def extractHtmlContent(content: String, tagName: String): String = {
    val docHtml = Jsoup.parse(content)
    val tab = docHtml.getElementsByTag(tagName)
    if (tab.isEmpty) {
      ""
    } else {
      tab.get(0).text()
    }
  }

  def extractTags(content: String): Iterator[String] = {
    val regex = "#[A-Za-z]+( |)".r
    ((regex findAllMatchIn content).map(a => a.matched.tail))

  }

}
