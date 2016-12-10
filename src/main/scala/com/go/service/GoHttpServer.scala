package com.go.service

import akka.Done
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.marshalling.{ToResponseMarshaller, ToResponseMarshallable}
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.go.entity.{Drivers, Driver, JsonSupport, Taxi}
import com.go.route.ResourceRouter
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future
import scala.io.StdIn

/**
 * Created by NKumar on 12/7/2016.
 */


//case class Driver(val chauffeurId: Float, val lattitude: Float, val longitude: Float, val distance: Long)
object GoHttpServer extends ResourceRouter {

  // domain model
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

  def main(args: Array[String]) {


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
            parameters('latitude.as[String], 'longitude.as[String], 'radius.as[String], 'limit.as[String]) { (latitude, longitude, radius, limit) =>
              println(latitude + "   " + longitude + "   " + radius + "   " + limit)
              val searchResult: Future[Drivers] = cabAgg.searchCabs(latitude.toLong, longitude.toLong, radius.toLong, limit.toInt)
                 complete(searchResult)
            }
          }
      } ~
        path(Segment / "location") { id =>
          get {
            println("Searching for Id" + id)
            val maybeItem: Future[Option[Taxi]] = cabAgg.searchCab(id.toLong)

            onSuccess(maybeItem) {
              case Some(taxi) => complete(taxi)
              case None => complete(StatusCodes.NotFound)
            }
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
}
