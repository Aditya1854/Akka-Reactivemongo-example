package connection

import connection.DatabaseConf.db1
import reactivemongo.api.{AsyncDriver, DB, MongoConnection}
import reactivemongo.api.bson.collection.BSONCollection
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object DatabaseConf {
  val mongoUri = "mongodb://127.0.0.1:27017"
  val driver = AsyncDriver()
  val parsedUri = MongoConnection.fromString(mongoUri)

  // Database and collections: Get references
  val futureConnection = parsedUri.flatMap(driver.connect(_))
  def db1: Future[DB] = futureConnection.flatMap(_.database("db1"))

}
object Connection {
  def studentQuery: Future[BSONCollection]  = db1.map(_.collection("student"))
  def universityQuery: Future[BSONCollection]  = db1.map(_.collection("university"))
  def userQuery: Future[BSONCollection]  = db1.map(_.collection("user"))
}

