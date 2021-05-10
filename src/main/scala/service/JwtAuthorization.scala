package service

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.{optionalHeaderValueByName, provide}
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import service.CorsHandle

object JwtAuthorization  {

  private val secretKey = "super_secret_key"
  private val header = JwtHeader("HS256")
  private val tokenExpiryPeriod = 1

  // needed to run the route
  implicit val system = ActorSystem(Behaviors.empty, "SprayExample")
  // needed for the future map/flatmap in the end and future in fetchItem and saveOrder
  implicit val executionContext = system.executionContext
  private val cors = new CorsHandle {}

  def generateToken(name:String, email: String): String = {
    val claims = JwtClaimsSet(
      Map(
        "firstName" -> name,
        "password" -> email
      )
    )
    JsonWebToken(header, claims, secretKey)
  }

  def authenticated: Directive1[Map[String, Any]] = {

    optionalHeaderValueByName("Authorization").flatMap { tokenFromUser =>

      val jwtToken = tokenFromUser.get.split(" ")
      jwtToken(1) match {
        case token if isTokenExpired(token) =>
          complete(StatusCodes.Unauthorized -> "Session expired.")

        case token if JsonWebToken.validate(token, secretKey) =>
          provide(getClaims(token))

        case _ => complete(StatusCodes.Unauthorized ->"Invalid Token")
      }
    }
  }

  private def isTokenExpired(jwt: String): Boolean =
    getClaims(jwt).get("expiredAt").exists(_.toLong < System.currentTimeMillis())

  private def getClaims(jwt: String): Map[String, String] =
    JsonWebToken.unapply(jwt) match {
      case Some(value) => value._2.asSimpleMap.getOrElse(Map.empty[String, String])
      case None => Map.empty[String, String]

    }


}
