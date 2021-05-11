package Repository

import scala.concurrent.Future
import reactivemongo.api.Cursor
import reactivemongo.api.bson.{BSONDocument, document}
import models.{Student, StudentJoin}
import connection.Connection.{studentQuery, universityQuery}
import models.BsonFormat.{studentJoinReader, studentReader, studentWriter}
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import scala.concurrent.ExecutionContext.Implicits.global


trait StudentRepository {

  def findAll(limit: Int = -1): Future[Seq[Student]] = {
    studentQuery.flatMap(
      _.find(BSONDocument(), Option.empty[Student])
        .cursor[Student]()
        .collect[Seq](limit, Cursor.FailOnError[Seq[Student]]())
    )
  }

  def createStudent(student: Student): Future[WriteResult] = {
    studentQuery.flatMap(_.insert.one(student))
  }

  def updateStudent(student: Student): Future[UpdateWriteResult] = {
    val selector = document("_id" -> student._id)
    // Update the matching student
    studentQuery.flatMap(_.update.one(selector, student))
  }

  def deleteStudent(id: Int): Future[WriteResult] = {
    val selector = document("_id" -> id)
    // delete the matching student
    studentQuery.flatMap(_.delete.one(selector))
  }

  def studentByUniversityName(): Future[List[StudentJoin]] = {

    def find(university: BSONCollection, student: BSONCollection): Future[List[StudentJoin]] = {
      import student.aggregationFramework.Lookup
      student.aggregatorContext[StudentJoin](
        Lookup(university.name, "universityId", "_id", "universityDetails")
      ).prepared.cursor.
        collect[List](-1, Cursor.FailOnError[List[StudentJoin]]())
    }

    universityQuery.flatMap(x => studentQuery.flatMap(y => find(x, y)))
  }
}
object StudentRepository extends StudentRepository

