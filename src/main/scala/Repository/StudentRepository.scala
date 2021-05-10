package Repository

import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.Cursor
import reactivemongo.api.bson.{BSONDocument, document}
import models._
import models.BsonFormat._
import connection.Connection._
import reactivemongo.api.bson.collection.BSONCollection

import scala.concurrent.ExecutionContext.Implicits.global


 class StudentRepository {

  def findAll(limit:Int = -1): Future[Seq[Student]]= {
    studentQuery.flatMap(
      _.find(BSONDocument(), Option.empty[Student])
        .cursor[Student]()
        .collect[Seq](limit, Cursor.FailOnError[Seq[Student]]())
    )
  }
  def createStudent(student:Student) = {
    studentQuery.flatMap(_.insert.one(student))
  }

  def updateStudent(student:Student)= {
    val selector = document("_id" -> student._id)
    // Update the matching student
    studentQuery.flatMap(_.update.one(selector, student))
  }
  def deleteStudent(id:Int)= {
    val selector = document("_id" -> id)
    // delete the matching student
    studentQuery.flatMap(_.delete.one(selector))
  }
  def studentByUniversityName() = {

    def find(university: BSONCollection,student: BSONCollection) = {
      import student.aggregationFramework.Lookup
      student.aggregatorContext[StudentJoin](
        Lookup(university.name, "universityId","_id", "universityDetails")
      ).prepared.cursor.
        collect[List](-1, Cursor.FailOnError[List[StudentJoin]]())
    }
    universityQuery.flatMap(x => studentQuery.flatMap(y => find(x,y)))
  }
}
object StudentRepository extends StudentRepository
