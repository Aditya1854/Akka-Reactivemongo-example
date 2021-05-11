package Repository

import scala.concurrent.Future
import reactivemongo.api.Cursor
import reactivemongo.api.bson.{BSONDocument, document}
import models.{University, UniversityJoin}
import connection.Connection.{studentQuery, universityQuery}
import models.BsonFormat.{universityJoinReader, universityReader, universityWriter}
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import scala.concurrent.ExecutionContext.Implicits.global

trait UniversityRepository {

  def findAll(limit: Int = -1): Future[Seq[University]] = {
    universityQuery.flatMap(
      _.find(BSONDocument(), Option.empty[University])
        .cursor[University]()
        .collect[Seq](limit, Cursor.FailOnError[Seq[University]]())
    )
  }

  def createUniversity(university: University): Future[WriteResult] =
    universityQuery.flatMap(_.insert.one(university))

  def updateUniversity(university: University): Future[UpdateWriteResult] = {
    val selector = document("_id" -> university._id)
    // Update the matching University
    universityQuery.flatMap(_.update.one(selector, university))
  }

  def deleteUniversity(id: Int): Future[WriteResult] = {
    val selector = document("_id" -> id)
    // Delete the matching University
    universityQuery.flatMap(_.delete.one(selector))
  }

  def universityAndNumberOfStudents(): Future[List[UniversityJoin]] = {
    def find(student: BSONCollection, university: BSONCollection): Future[List[UniversityJoin]] = {
      import university.aggregationFramework.Lookup
      university.aggregatorContext[UniversityJoin](
        Lookup(student.name, "_id", "universityId", "students")
      ).prepared.cursor.
        collect[List](-1, Cursor.FailOnError[List[UniversityJoin]]())
    }

    universityQuery.flatMap(x => studentQuery.flatMap(y => find(y, x)))
  }
}
object UniversityRepository extends UniversityRepository