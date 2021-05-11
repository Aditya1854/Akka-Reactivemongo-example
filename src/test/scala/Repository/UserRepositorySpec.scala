package Repository

import models._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.MockitoSugar
import reactivemongo.api.commands.DefaultWriteResult
import scala.concurrent.Future

class UserRepositorySpec extends AnyWordSpec with UniversityRepository with Matchers with ScalaFutures with ScalatestRouteTest with MockitoSugar {

  val userRepository: UserRepository = mock[UserRepository]
  val userLogin: UserData = UserData("aditya@gmail.com", "1234567")
  val user: User = User("Aditya", "Kumar", "aditya@gmail.com", "1234567")
  val t: Boolean = true

  "User Repository test cases" should {

    "create user" in {
      when(userRepository.createUser(user)) thenReturn Future.successful(DefaultWriteResult(t, 1, List(), None, None, None))
      val response = userRepository.createUser(user)
      whenReady(response) {
        userData =>
          assert(userData.ok === t)
      }
    }

    "validate user" in {
      when(userRepository.findOne(userLogin.email, userLogin.password)) thenReturn Future.successful(Some(user))
      val response = userRepository.findOne(userLogin.email, userLogin.password)
      whenReady(response) {
        userData =>
          assert(userData === Some(User("Aditya", "Kumar", "aditya@gmail.com", "1234567")))
      }
    }
  }

}
