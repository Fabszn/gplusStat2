package model

import reactivemongo.bson.{BSONDocumentWriter, BSONDocument, BSONDocumentReader}

/**
 * Created by fsznajderman on 07/12/2013.
 */
case class Tag(lbl: String, articleIds:List[String])


object Tag {

  implicit object TagReader extends BSONDocumentReader[Tag]{
    def read(bson: BSONDocument): Tag = ???
  }
  implicit object TagWriter extends BSONDocumentWriter[Tag]{
    def write(t: Tag): BSONDocument = {
      BSONDocument()


    }
  }


}
