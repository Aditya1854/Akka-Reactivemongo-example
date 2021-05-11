package Repository

import models._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.MockitoSugar
import reactivemongo.api.commands.{DefaultWriteResult, UpdateWriteResult}
import scala.concurrent.Future

class UniversityRepositorySpec extends AnyWordSpec with UniversityRepository with Matchers with ScalaFutures with ScalatestRouteTest with MockitoSugar {


  val universityRepository: UniversityRepository = mock[UniversityRepository]
  val university: List[University] = List(University(1, "HCU", "hyderabada"))
  val universityJoin: List[UniversityJoin] = List(UniversityJoin(1, "HCU", "hyderabada", Array(Student(1, "aditya", "aditya@gmail.comm", 1, "1996-05-02"))))
  val t: Boolean = true

  "University Repository test cases" should {

    "return list of all student" in {
      when(universityRepository.findAll()) thenReturn Future.successful(university)
      val response = universityRepository.findAll()
      whenReady(response) {
        universityData =>
          assert(universityData === Seq(university.head))
      }

    }

    "return list of all student with universityName" in {
      when(universityRepository.universityAndNumberOfStudents()) thenReturn Future.successful(universityJoin)
      val response = universityRepository.universityAndNumberOfStudents()
      whenReady(response) {
        universityData =>
          assert(universityData === Seq(universityJoin.head))
      }
    }

    "create student" in {
      when(universityRepository.createUniversity(university.head)) thenReturn Future.successful(DefaultWriteResult(t, 1, List(), None, None, None))
      val response = universityRepository.createUniversity(university.head)
      whenReady(response) {
        universityData =>
          assert(universityData.ok === t)
      }
    }

    "update student" in {
      when(universityRepository.updateUniversity(university.head)) thenReturn Future.successful(UpdateWriteResult(t, 1, 1, List(), List(), None, None, None))
      val response = universityRepository.updateUniversity(university.head)
      whenReady(response) {
        universityData =>
          assert(universityData.ok === t)
      }
    }

    "delete student" in {
      when(universityRepository.deleteUniversity(1)) thenReturn Future.successful(DefaultWriteResult(t, 1, List(), None, None, None))
      val response = universityRepository.deleteUniversity(1)
      whenReady(response) {
        universityData =>
          assert(universityData.ok === t)
      }
    }
  }
}
