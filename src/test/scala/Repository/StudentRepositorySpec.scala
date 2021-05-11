package Repository

import models._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.MockitoSugar
import reactivemongo.api.commands.{DefaultWriteResult, UpdateWriteResult}
import scala.concurrent.Future

class StudentRepositorySpec extends AnyWordSpec with StudentRepository with Matchers with ScalaFutures with ScalatestRouteTest with MockitoSugar {
  val studentRepository: StudentRepository = mock[StudentRepository]
  val student: List[Student] = List(Student(1, "aditya lsls", "aditya@gmail.comm", 1, "1996-05-02"))
  val studentJoin: List[StudentJoin] = List(StudentJoin(1, "aditya", "aditya@gmail.comm", 1, "1996-05-02", Array(University(1, "HCU", "hyderabad"))))
  val t: Boolean = true

  "Student Repository test case" should {

    "return list of all student" in {
      when(studentRepository.findAll()) thenReturn Future.successful(student)
      val response = studentRepository.findAll()
      whenReady(response) {
        studentData =>
          assert(studentData === Seq(student.head))
      }

    }

    "return list of all student with universityName" in {
      when(studentRepository.studentByUniversityName()) thenReturn Future.successful(studentJoin)
      val response = studentRepository.studentByUniversityName()
      whenReady(response) {
        studentData =>
          assert(studentData === Seq(studentJoin.head))
      }
    }

    "create student" in {
      when(studentRepository.createStudent(student.head)) thenReturn Future.successful(DefaultWriteResult(t, 1, List(), None, None, None))
      val response = studentRepository.createStudent(student.head)
      whenReady(response) {
        studentData =>
          assert(studentData.ok === t)
      }
    }

    "update student" in {
      when(studentRepository.updateStudent(student.head)) thenReturn Future.successful(UpdateWriteResult(t, 1, 1, List(), List(), None, None, None))
      val response = studentRepository.updateStudent(student.head)
      whenReady(response) {
        studentData =>
          assert(studentData.ok === t)
      }
    }

    "delete student" in {
      when(studentRepository.deleteStudent(1)) thenReturn Future.successful(DefaultWriteResult(t, 1, List(), None, None, None))
      val response = studentRepository.deleteStudent(1)
      whenReady(response) {
        studentData =>
          assert(studentData.ok === t)
      }
    }
  }
}
