package service

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.{optionalHeaderValueByName, provide}
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}

trait JwtAuthorization {

  private val secretKey = "super_secret_key"
  private val header = JwtHeader("HS256")
  //  private val tokenExpiryPeriod = 1


  def generateToken(name: String, email: String): String = {
    val claims = JwtClaimsSet(
      Map(
        "firstName" -> name,
        "password" -> email
      )
    )
    JsonWebToken(header, claims, secretKey)
  }

  def authenticated: Directive1[Map[String, Any]] = {
    optionalHeaderValueByName("Authorization").flatMap {
      case Some(tokenFromUser) =>
        val jwtToken = tokenFromUser.split(" ")
        jwtToken(1) match {
          case token if isTokenExpired(token) =>
            complete(StatusCodes.Unauthorized -> "Session expired.")

          case token if JsonWebToken.validate(token, secretKey) =>
            provide(getClaims(token))
          case _ => complete(StatusCodes.Unauthorized -> "Invalid Token")
        }

      case None => complete(StatusCodes.Unauthorized -> "Token missing")
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
