package service

import models.JsonFormat._
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import Repository._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes.BadRequest
import models.{Student, StudentData, University, UniversityData, User, UserData, UserName}

import scala.io.StdIn


object AkkaHttpDemo {

  // needed to run the route
  implicit val system = ActorSystem(Behaviors.empty, "SprayExample")
  // needed for the future map/flatmap in the end and future in fetchItem and saveOrder
  implicit val executionContext = system.executionContext
  val allowCors = new  CorsHandle{}

  def main(args: Array[String]): Unit = {
    val route =
      path("student"/"") {
        options{
          allowCors.corsHandler(complete(StatusCodes.OK))
        }~
        get {JwtAuthorization.authenticated { _ =>
          allowCors.corsHandler(complete {
            StudentRepository.findAll().map { res =>
              write(res)
            }
          })
        }
        }
      }~
        path("student"/"list") {
          options{
            allowCors.corsHandler(complete(StatusCodes.OK))
          }~
          get {
            JwtAuthorization.authenticated { _ =>
              allowCors.corsHandler(complete {
                StudentRepository.studentByUniversityName().map { res =>
                  val result = for (x <- res) yield StudentData(x._id, x.name, x.email, x.universityDetails(0).name, x.DOB)
                  write(result)
                }
              })
            }
          }
        }~
        path("student"/"") {
          options{
            allowCors.corsHandler(complete(StatusCodes.OK))
          }~
          post {JwtAuthorization.authenticated { _ =>
            entity(as[String]) { // post body parameter
              studentRoute =>
                allowCors.corsHandler(complete {
                  val student = parse(studentRoute).extract[Student]
                  StudentRepository.createStudent(student).map { res =>
                    res.ok.toString
                  }
                })
            }
          }
          }
        }~
        path("student"/"") {
          options{
            allowCors.corsHandler(complete(StatusCodes.OK))
          }~
          put {JwtAuthorization.authenticated { _ =>
            entity(as[String]) { // post body parameter
              studentRoute =>
                allowCors.corsHandler(complete {
                  val student = parse(studentRoute).extract[Student]
                  StudentRepository.updateStudent(student).map { res =>
                    res.ok.toString
                  }
                })
            }
          }
          }
        }~
        path("student") {
          options{
            allowCors.corsHandler(complete(StatusCodes.OK))
          }~
          parameters('id.as[Int]) { id => // URL parameter
            delete {JwtAuthorization.authenticated { _ =>
              allowCors.corsHandler(complete {

                StudentRepository.deleteStudent(id).map { res =>
                  res.ok.toString
                }
              })
            }
            }
          }
        }~
        path("university"/"") {
          options{
            allowCors.corsHandler(complete(StatusCodes.OK))
          }~
          get {JwtAuthorization.authenticated { _ =>
            allowCors.corsHandler(complete {
              UniversityRepository.findAll().map { res =>
                write(res)
              }
            })
          }
          }
        }~
          path("university"/"list") {
            options{
              allowCors.corsHandler(complete(StatusCodes.OK))
            }~
            get {JwtAuthorization.authenticated { _ =>
              allowCors.corsHandler(complete{
                UniversityRepository.universityAndNumberOfStudents().map{ res=>
                  val result = for(x <- res) yield UniversityData(x._id,x.name,x.location,x.students.length)
                  write(result)
                }
              })
            }
            }
          }~
        path("university"/"") {
          options{
            allowCors.corsHandler(complete(StatusCodes.OK))
          }~
          post {JwtAuthorization.authenticated { _ =>
            entity(as[String]) { // post body parameter
              universityRoute =>
                allowCors.corsHandler(complete {
                  val university = parse(universityRoute).extract[University]
                  UniversityRepository.createUniversity(university).map { res =>
                    res.ok.toString
                  }
                })
            }
          }
          }
        }~
        path("university"/"") {
          options{
            allowCors.corsHandler(complete(StatusCodes.OK))
          }~
          put {JwtAuthorization.authenticated { _ =>
            entity(as[String]) { // post body parameter
              universityRoute =>
                allowCors.corsHandler(complete {
                  val university = parse(universityRoute).extract[University]
                  UniversityRepository.updateUniversity(university).map { res =>
                    res.ok.toString
                  }
                })
            }
          }
          }
        }~
        path("university") {
          options{
            allowCors.corsHandler(complete(StatusCodes.OK))
          }~
          parameters('id.as[Int]) { id => // URL parameter
            delete {JwtAuthorization.authenticated { _ =>
              allowCors.corsHandler(complete {
                UniversityRepository.deleteUniversity(id).map { res =>
                  res.ok.toString
                }
              })
            }
            }
          }
        }~
          path("user"/"") {
            options{
              allowCors.corsHandler(complete(StatusCodes.OK))
            }~
            post {
              entity(as[String]) { // post body parameter
                userRoute =>
                  allowCors.corsHandler(complete {
                    val user = parse(userRoute).extract[User]
                    UserRepository.createUser(user).map{ res =>
                      res.ok.toString
                    }
                  })
              }
            }
          }~
          path("users"/"") {
            options{
              allowCors.corsHandler(complete(StatusCodes.OK))
            }~
            post {
              entity(as[String]) { // post body parameter
                userRoute =>
                  allowCors.corsHandler(complete {
                    val user = parse(userRoute).extract[UserData]
                    UserRepository.findOne(user.email,user.password).map{
                      case Some(user) =>
                        write(Map("token" ->JwtAuthorization.generateToken(user.firstName,user.email)))
//                            write(Map("token" ->"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MjA3MDI4ODEsImlhdCI6MTYyMDYxNjQ4MSwKICAiZmlyc3ROYW1lIiA6ICJBZGl0eWEiLAogICJlbWFpbCIgOiAiYWRpdHlhQGdtYWlsLmNvbSIKfQ.wZ_65RZUZ2cQw2cwlP9A1FKUjAoxrzMifb-Yyu05cZ8"))
                      case None =>
                        write(BadRequest)


                    }
                  })
              }
            }
          }


    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}

