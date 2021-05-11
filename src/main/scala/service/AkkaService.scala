package service

import models.JsonFormat._
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.{Http, server}
import akka.http.scaladsl.server.Directives._
import Repository._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes.BadRequest
import models.{Student, StudentData, University, UniversityData, User, UserData}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn


trait AkkaHttpDemo extends JwtAuthorization {

  implicit val executionContext: ExecutionContextExecutor
  val allowCors: CorsHandle = new CorsHandle {}
  val studentRepository: StudentRepository
  val universityRepository: UniversityRepository
  val userRepository : UserRepository
  val route: server.Route =
    path("student" / "simplelist") {
      options {
        allowCors.corsHandler(complete(StatusCodes.OK))
      } ~
        get {
          authenticated { _ =>
            allowCors.corsHandler(complete {
              studentRepository.findAll().map { res =>
                write(res)
              }
            })
          }
        }
    } ~
      path("student" / "list") {
        options {
          allowCors.corsHandler(complete(StatusCodes.OK))
        } ~
          get {
            authenticated { _ =>
              allowCors.corsHandler(complete {
                studentRepository.studentByUniversityName().map { res =>
                  val result = for (x <- res) yield StudentData(x._id, x.name, x.email, x.universityDetails(0).name, x.DOB)
                  write(result)
                }
              })
            }
          }
      } ~
      path("student" / "create") {
        options {
          allowCors.corsHandler(complete(StatusCodes.OK))
        } ~
          post {
            authenticated { _ =>
              entity(as[String]) {
                studentRoute =>
                  allowCors.corsHandler(complete {
                    val student = parse(studentRoute).extract[Student]
                    studentRepository.createStudent(student).map { res =>
                      res.ok.toString
                    }
                  })
              }
            }
          }
      } ~
      path("student" / "update") {
        options {
          allowCors.corsHandler(complete(StatusCodes.OK))
        } ~
          put {
            authenticated { _ =>
              entity(as[String]) {
                studentRoute =>
                  allowCors.corsHandler(complete {
                    val student = parse(studentRoute).extract[Student]
                    studentRepository.updateStudent(student).map { res =>
                      res.ok.toString
                    }
                  })
              }
            }
          }
      } ~
      path("student"/"delete") {
        options {
          allowCors.corsHandler(complete(StatusCodes.OK))
        } ~
          parameters('id.as[Int]) { id =>
            delete {
              authenticated { _ =>
                allowCors.corsHandler(complete {

                  studentRepository.deleteStudent(id).map { res =>
                    res.ok.toString
                  }
                })
              }
            }
          }
      } ~
      path("university" / "simplelist") {
        options {
          allowCors.corsHandler(complete(StatusCodes.OK))
        } ~
          get {
            authenticated { _ =>
              allowCors.corsHandler(complete {
                universityRepository.findAll().map { res =>
                  write(res)
                }
              })
            }
          }
      } ~
      path("university" / "list") {
        options {
          allowCors.corsHandler(complete(StatusCodes.OK))
        } ~
          get {
            authenticated { _ =>
              allowCors.corsHandler(complete {
                universityRepository.universityAndNumberOfStudents().map { res =>
                  val result = for (x <- res) yield UniversityData(x._id, x.name, x.location, x.students.length)
                  write(result)
                }
              })
            }
          }
      } ~
      path("university" / "create") {
        options {
          allowCors.corsHandler(complete(StatusCodes.OK))
        } ~
          post {
            authenticated { _ =>
              entity(as[String]) {
                universityRoute =>
                  allowCors.corsHandler(complete {
                    val university = parse(universityRoute).extract[University]
                    universityRepository.createUniversity(university).map { res =>
                      res.ok.toString
                    }
                  })
              }
            }
          }
      } ~
      path("university" / "update") {
        options {
          allowCors.corsHandler(complete(StatusCodes.OK))
        } ~
          put {
            authenticated { _ =>
              entity(as[String]) {
                universityRoute =>
                  allowCors.corsHandler(complete {
                    val university = parse(universityRoute).extract[University]
                    universityRepository.updateUniversity(university).map { res =>
                      res.ok.toString
                    }
                  })
              }
            }
          }
      } ~
      path("university"/"delete") {
        options {
          allowCors.corsHandler(complete(StatusCodes.OK))
        } ~
          parameters('id.as[Int]) { id =>
            delete {
              authenticated { _ =>
                allowCors.corsHandler(complete {
                  universityRepository.deleteUniversity(id).map { res =>
                    res.ok.toString
                  }
                })
              }
            }
          }
      } ~
      path("user" / "create") {
        options {
          allowCors.corsHandler(complete(StatusCodes.OK))
        } ~
          post {
            entity(as[String]) {
              userRoute =>
                allowCors.corsHandler(complete {
                  val user = parse(userRoute).extract[User]
                  userRepository.createUser(user).map { res =>
                    res.ok.toString
                  }
                })
            }
          }
      } ~
      path("user" / "validate") {
        options {
          allowCors.corsHandler(complete(StatusCodes.OK))
        } ~
          get {
            entity(as[String]) {
              userRoute =>
                allowCors.corsHandler(complete {
                  val user = parse(userRoute).extract[UserData]
                  userRepository.findOne(user.email, user.password).map {
                    case Some(user) =>
                      write(Map("token" -> generateToken(user.firstName, user.email)))
                    case None =>
                      write(BadRequest)


                  }
                })
            }
          }
      }
}

object Route extends App with AkkaHttpDemo {
  val studentRepository:StudentRepository = StudentRepository
  val universityRepository : UniversityRepository = UniversityRepository
  val userRepository :UserRepository = UserRepository
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "SprayExample")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  val bindingFuture : Future[Http.ServerBinding] = Http().newServerAt("localhost", 8080).bind(route)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}

