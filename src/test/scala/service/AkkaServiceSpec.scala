package service

import Repository.{StudentRepository, UniversityRepository, UserRepository}
import models.JsonFormat._
import models.BsonFormat._
import models._
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.provide
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.MockitoSugar
import reactivemongo.api.commands.{DefaultWriteResult, UpdateWriteResult}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration.FiniteDuration

class AkkaServiceSpec extends AnyWordSpec with Matchers with ScalaFutures with ScalatestRouteTest with AkkaHttpDemo with MockitoSugar {
  override val userRepository: UserRepository = mock[UserRepository]
  override val universityRepository: UniversityRepository = mock[UniversityRepository]
  override val studentRepository: StudentRepository = mock[StudentRepository]

  implicit val routeTimeout: RouteTestTimeout = RouteTestTimeout(FiniteDuration(15, "seconds"))
  override implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val res = Map("hello"->"one")
  override def authenticated: Directive1[Map[String, Any]]  ={ provide(res)}

  val student = List(Student(1,"aditya","aditya@gmail.comm",1,"1996-05-02"))
  val studentJoin = List(StudentJoin(1,"aditya","aditya@gmail.comm",1,"1996-05-02",Array(University(1,"HCU","hyderabad"))))
  val university = List(University(1,"HCU","hyderabada"))
  val universityJoin = List(UniversityJoin(1,"HCU","hyderabada",Array(Student(1,"aditya","aditya@gmail.comm",1,"1996-05-02"))))

  "UserRoutes test all routes of user" should {
    "create the user" in {
      val user = User("Aditya","Kumar","aditya@gmail.com","123456")
      when(userRepository.createUser(user)) thenReturn Future.successful(DefaultWriteResult(true,1,List(),None,None,None))
      Post("/user/create",write(user)) ~> route ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should === ("""true""")
      }
    }

    "validate the User login" in {
      val userLogin = UserData("aditya@gmail.com","1234567")
      when(userRepository.findOne("aditya@gmail.com","1234567")) thenReturn Future.successful(Some(User("Aditya","Kumar","aditya@gmail.com","123456")))
      Get("/user/validate",write(userLogin)) ~> route ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should === ("""{"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaXJzdE5hbWUiOiJBZGl0eWEiLCJwYXNzd29yZCI6ImFkaXR5YUBnbWFpbC5jb20ifQ.QWerVNtJ03YikWRmdYq5g3I9ZFKwlqlrWQEQAfU0zlQ"}""")
      }
    }

  }
  "Student Routes test all routes of student" should{

    "return list of all student" in {
      when(studentRepository.findAll()) thenReturn Future.successful(student)
      Get("/student/simplelist") ~> route ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should ===("""[{"_id":1,"name":"aditya","email":"aditya@gmail.comm","universityId":1,"DOB":"1996-05-02"}]""")
      }
    }

    "return list of all student with the university name" in {
      when(studentRepository.studentByUniversityName()) thenReturn Future.successful(studentJoin)
      Get("/student/list") ~> route ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should ===("""[{"_id":1,"name":"aditya","email":"aditya@gmail.comm","universityName":"HCU","DOB":"1996-05-02"}]""")
      }
    }

    "create a student" in {
      when(studentRepository.createStudent(student(0))) thenReturn Future.successful(DefaultWriteResult(true,1,List(),None,None,None))
      Post("/student/create",write(student(0))) ~> route ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should ===("""true""")
      }
    }

    "update a student" in {
      when(studentRepository.updateStudent(student(0))) thenReturn Future.successful(UpdateWriteResult(true,1,1,List(),List(),None,None,None))
      Put("/student/update",write(student(0))) ~> route ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should ===("""true""")
      }
    }

    "delete a student" in {
      when(studentRepository.deleteStudent(1)) thenReturn Future.successful(DefaultWriteResult(true,1,List(),None,None,None))
      Delete("/student/delete?id=1") ~> route ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should ===("""true""")
      }
    }
  }

  "University Routes test all routes of university" should{

    "return list of all university" in {
      when(universityRepository.findAll()) thenReturn Future.successful(university)
      Get("/university/simplelist") ~> route ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should ===("""[{"_id":1,"name":"HCU","location":"hyderabada"}]""")
      }
    }

    "return list of all university with it's counts " in {
      when(universityRepository.universityAndNumberOfStudents()) thenReturn Future.successful(universityJoin)
      Get("/university/list") ~> route ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should ===("""[{"_id":1,"name":"HCU","location":"hyderabada","counts":1}]""")
      }
    }

    "create a university" in {
      when(universityRepository.createUniversity(university(0))) thenReturn Future.successful(DefaultWriteResult(true,1,List(),None,None,None))
      Post("/university/create",write(university(0))) ~> route ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should ===("""true""")
      }
    }

    "update a university" in {
      when(universityRepository.updateUniversity(university(0))) thenReturn Future.successful(UpdateWriteResult(true,1,1,List(),List(),None,None,None))
      Put("/university/update",write(university(0))) ~> route ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should ===("""true""")
      }
    }

    "delete a university" in {
      when(universityRepository.deleteUniversity(1)) thenReturn Future.successful(DefaultWriteResult(true,1,List(),None,None,None))
      Delete("/university/delete?id=1") ~> route ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should ===("""true""")
      }
    }


  }

}
