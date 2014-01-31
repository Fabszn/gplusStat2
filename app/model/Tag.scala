package model

import reactivemongo.bson._
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.collections.default.BSONCollection
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import play.api.Play.current

/**
 * Created by fsznajderman on 07/12/2013.
 */
case class Tag(_id: Option[BSONObjectID] = None, lbl: String, articleIds: List[String])


object Tag {

  implicit object TagReader extends BSONDocumentReader[Tag] {


    def read(document: BSONDocument): Tag =
      Tag(document.getAs[BSONObjectID]("_id"),
        document.getAs[BSONString]("lblTag").getOrElse(BSONString("NO_VALUE")).value,
        document.getAs[List[String]]("articles").toList.flatten
      )
  }

  implicit object TagWriter extends BSONDocumentWriter[Tag] {
    def write(t: Tag): BSONDocument = {
      BSONDocument(
        "_id" -> t._id.getOrElse(BSONObjectID.generate),
        "lblTag" -> t.lbl,
        "articleIds" -> t.articleIds
      )
    }
  }


  def addArticleToTag(tag: String, articleId: String):Future[Option[Tag]] = {
    findTagById(BSONObjectID(tag))
  }

  def findTagById(_id: BSONObjectID): Future[Option[Tag]] = {
    queryTag(BSONDocument("_id" -> _id))
  }

  private def queryTags(query: BSONDocument): Future[List[Tag]] = {

    val tags = ReactiveMongoPlugin.db.collection[BSONCollection]("Tag")
    val cursor = tags.find(query).cursor[Tag]
    cursor.collect[List]()

  }

  private def queryTag(query: BSONDocument): Future[Option[Tag]] = {

    val tags = ReactiveMongoPlugin.db.collection[BSONCollection]("Tag")
    val cursor = tags.find(query).cursor[Tag]
    cursor.headOption

  }

}
