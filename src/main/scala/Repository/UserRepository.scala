package Repository

import scala.concurrent.Future
import reactivemongo.api.bson.BSONDocument
import models.User
import connection.Connection.userQuery
import models.BsonFormat.{userWriter, userReader}
import reactivemongo.api.commands.WriteResult
import scala.concurrent.ExecutionContext.Implicits.global


trait UserRepository {

  def createUser(user: User): Future[WriteResult] = userQuery.flatMap(_.insert.one(user))

  def findOne(email: String, password: String): Future[Option[User]] = {
    userQuery.flatMap(_.find(BSONDocument("email" -> email, "password" -> password), Option.empty[User]).one[User])
  }

}
object UserRepository extends UserRepository

