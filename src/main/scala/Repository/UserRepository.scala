package Repository

import scala.concurrent.Future
import reactivemongo.api.Cursor
import reactivemongo.api.bson.{BSONDocument, document}
import models._
import models.BsonFormat._
import connection.Connection._
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.WriteResult
import scala.concurrent.ExecutionContext.Implicits.global


class UserRepository {

  def createUser(user:User): Future[WriteResult] = userQuery.flatMap(_.insert.one(user))

  def findOne(email:String,password:String): Future[Option[User]] = {
    userQuery.flatMap(_.find(BSONDocument("email"->email,"password"->password), Option.empty[User]).one[User])
  }

}
object UserRepository extends UserRepository