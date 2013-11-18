package controllers

import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.services.plus.Plus

import com.google.api.client.json.jackson.JacksonFactory
import com.google.api.services.plus.model.Activity
import java.util
import scala.collection.{mutable, JavaConversions}


/**
 * User: fsznajderman
 * Date: 15/11/2013
 * Time: 23:53
 */

object GooglePlusApiHelper {

  def getArticles(): mutable.Buffer[Activity] = {


    val jsonFactory = new JacksonFactory
    val httpTransport = new NetHttpTransport

    val plus = new Plus(httpTransport, jsonFactory, null)

    val listActivities = plus.activities()
      .list("112440333946538821016", "public")
      .setFields("nextPageToken,items(id,url,published,object(content,plusoners,resharers))"  )
      .setMaxResults(100L)
      .setKey("AIzaSyALxct37VoDkvswBYOysbMiRpO5hjVFukg")

    // Execute and process the next page request
    var feed = listActivities.execute()
    val articles = new util.ArrayList[Activity]
    var activities = feed.getItems

    while (activities != null) {
      //val acti = JavaConversions.asScalaBuffer(activities)
      articles.addAll(activities)
      println(articles.size())

      if (feed.getNextPageToken == null) {
          println("null")
          activities = null
      } else {
        println("not null")
        listActivities.setPageToken(feed.getNextPageToken)
        // Execute and process the next page request
        feed = listActivities.execute()
        activities = feed.getItems
      }
    }

    JavaConversions.asScalaBuffer(articles)
  }
}



