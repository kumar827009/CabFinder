package com.go.service

import akka.Done
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.go.entity.{Driver, Drivers, JsonSupport, Taxi}
import com.go.req.Validator
import com.go.route.ResourceRouter
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future
import scala.io.StdIn

/**
 * Created by NKumar on 12/7/2016.
 */

object GoHttpServer extends App with ResourceRouter with Validator {

  /*
    Step 1: Create Akka Actor System
    Akka-http use Actor model to process the request response scenarios i.e conncurrent request will be handled by the Actor system. This must be created as a 1st part.
  */

  /*
    Step 2:Create Actor Materiliaze
    Akka HTTP uses akka reactive streams for stream processing on TPC.
    So in a reactive system,we need to specify flow materializer which specifies the how requests/repose flow get processed.
    In akka-http, actors will be used for handling request and response flows. So we use ActorMaterializer here.
*/


  /*
  Step 3: Create API route.
  Route specifies the URI endpoints REST server exposing. It is combination of multiple paths.
  A simple path has the following three parts
  1.Directive/URI
  2.HTTP Method
  3.Response

   the below example:-
  1. / is the directive
  2.  get is HTTP Method
  3.  Returns json as the response
*/

  implicit val taxiFormat = JsonSupport.jsonFormat4(Taxi)


  implicit val executionContext = system.dispatcher
  implicit val driver = JsonSupport.jsonFormat4(Driver)
  implicit val drivers = JsonSupport.jsonFormat1(Drivers)

  def fetchTaxi(taxi: Long): Future[Option[Taxi]] = ???

  var taxis = Vector.empty[Taxi]

  def saveTaxi(taxi: Taxi): Future[Done] = {
    taxis = taxi +: taxis
    Future(Done)
  }


  val cabAgg: LocationService = new LocationService


  def route: Route = pathPrefix("drivers") {
    pathEnd {
      post {
        entity(as[Taxi]) { taxi =>
          println(taxi.longitude + "   " + taxi.lattitude)
          val saved: Future[Done] = cabAgg.cabAggregate(taxi) //saveTaxi(taxi)
          onComplete(saved) { done =>
            complete("Taxi Details Added")
          }
        }
      } ~
        get {
          parameters('latitude.as[Float], 'longitude.as[Float], 'radius.*, 'limit.as[Int]) { (latitude, longitude, radius, limit) =>
            println(latitude + "   " + longitude + "   " + radius + "   " + limit)
            validateRequest(latitude, longitude, radius.toList) match {
              case Some("valid") =>
                val searchResult: Future[Drivers] = cabAgg.searchCabs(latitude, longitude, radius.toList, limit.toInt)
                complete(searchResult)
              case Some("invalid") =>
                complete(400, "Latitude should be between +/- 90")
            }

          }
        }
    } ~
      path(Segment / "location") { id =>
        get {
          println("Searching for Id" + id)
          validateDriverId(id.toInt) match {
            case Some("valid") =>
              //val searchResult: Future[Option[Taxi]] = cabAgg.searchCab(id.toInt)
              val maybeItem: Future[Option[Taxi]] = cabAgg.searchCab(id.toInt)
              onSuccess(maybeItem) {
                case Some(taxi) => complete(taxi)
                case None => complete(StatusCodes.NotFound)
              }
            case Some("invalid") =>
              complete(400, "Latitude should be between +/- 90")
          }

          //---------
          /*val maybeItem: Future[Option[Taxi]] = cabAgg.searchCab(id.toLong)

          onSuccess(maybeItem) {
            case Some(taxi) => complete(taxi)
            case None => complete(StatusCodes.NotFound)
          }*/
        } ~
          put {
            entity[Taxi](as[Taxi]) { taxi =>
              println(taxi.longitude + "   " + taxi.lattitude)
              val saved: Future[Done] = cabAgg.updateCab(id.toLong, taxi) //saveTaxi(taxi)
              onComplete(saved) { done =>
                complete("Taxi Details Updated")
              }
            }
          }
      }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8282)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
