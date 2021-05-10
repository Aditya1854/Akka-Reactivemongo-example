package models

import org.json4s.{DefaultFormats, JValue}
import org.json4s.native.{JsonMethods, Serialization}
import reactivemongo.api.bson.{BSONDocumentReader, BSONDocumentWriter, Macros}

import java.sql.Date

object BsonFormat {

  implicit def studentWriter: BSONDocumentWriter[Student] = Macros.writer[Student]
  implicit def studentReader: BSONDocumentReader[Student] = Macros.reader[Student]

  implicit def studentJoinWriter: BSONDocumentWriter[StudentJoin] = Macros.writer[StudentJoin]
  implicit def studentJoinReader: BSONDocumentReader[StudentJoin] = Macros.reader[StudentJoin]

  implicit def universityWriter: BSONDocumentWriter[University] = Macros.writer[University]
  implicit def universityReader: BSONDocumentReader[University] = Macros.reader[University]

  implicit def universityJoinWriter: BSONDocumentWriter[UniversityJoin] = Macros.writer[UniversityJoin]
  implicit def universityJoinReader: BSONDocumentReader[UniversityJoin] = Macros.reader[UniversityJoin]

  implicit def userWriter: BSONDocumentWriter[User] = Macros.writer[User]
  implicit def userReader: BSONDocumentReader[User] = Macros.reader[User]

  implicit def userDataWriter: BSONDocumentWriter[UserData] = Macros.writer[UserData]
  implicit def userDataReader: BSONDocumentReader[UserData] = Macros.reader[UserData]

  implicit def userNameWriter: BSONDocumentWriter[UserName] = Macros.writer[UserName]
  implicit def userNameReader: BSONDocumentReader[UserName] = Macros.reader[UserName]

}

object JsonFormat {

  implicit val formats = DefaultFormats

  def write[T <: AnyRef](value: T): String = {
    Serialization.write(value)
  }

  def parse(value: String): JValue = {
    JsonMethods.parse(value)
  }


}