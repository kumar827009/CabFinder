package com.go.service

import java.sql.{DriverManager, PreparedStatement, ResultSet}

import akka.Done
import com.go.entity.model.DataAccessLayer
import com.go.entity.{Driver, Drivers, Taxi}
import slick.driver.MySQLDriver.api._

import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

/**
 * Created by NKumar on 12/3/2016.
 * The
 */
class LocationService(implicit val executionContext: ExecutionContext) {

  val cabFinderQuery = "SELECT id,lat,lng, ( 6371 * acos( cos( radians(?) ) * cos( radians( lat ) ) * cos( radians( lng ) - radians(?) ) + sin( radians(?) ) * sin( radians( lat ) ) ) ) AS distance FROM drivers HAVING distance < ? ORDER BY distance LIMIT 0 , ?"

  Class.forName("com.mysql.jdbc.Driver");
  val con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Location", "root", "admin")
  var locations = new mutable.HashMap[Long, Taxi]()
  val db = Database.forConfig("mySqlDB")
  /*
    Dividing the entire area in to 4 quadrant.
    Algorithm:
    Step1: Based on the latitude and longitude. Find out which quadrant the user (one who is searching for a cab lies)

   */

  /* If Latitude <=20 (Quadrant III and IV are the possible candidates for searching.
  *
  *     A) If Longitude >=32 : II Quadrant
  *     B) If Longitude >=32 : I Quadrant
  *
  *
  * If Latitude > 20 (Quadrant I and II are the possible candidates for searching.
  *   A) If Longitude >=32 : III Quadrant
  *   B) If Longitude >=32 : IV Quadrant
  *
  *
  * */

  /* Quadrant I */
  var geoArea1 = Vector.empty[Taxi]

  /* Quadrant II */
  var geoArea2 = Vector.empty[Taxi]

  /* Quadrant III */
  var geoArea3 = Vector.empty[Taxi]

  /* Quadrant IV */
  var geoArea4 = Vector.empty[Taxi]

  def updateLocation(lat: Float, lng: Float, id: Int) = sql"update location.drivers set lat=$lat,lng=$lng where id =$id".asUpdate

  def searchRes = (Int, Float, Float, Long)

  def searchDrivers(lat: Double, lng: Double, radius: Long, limit: Int) = sql"SELECT id,lat,lng, ( 3959 * acos( cos( radians($lat) ) * cos( radians( lat ) ) * cos( radians( lng ) - radians($lng) ) + sin( radians($lat) ) * sin( radians( lat ) ) ) ) AS distance FROM drivers HAVING distance < $radius ORDER BY distance LIMIT 0 , $limit".as[(Int, Float, Float, Long)]

  /**
   * This method adds a cab location to the collection.
   * @param taxi Cab resource to add
   * @return Future[Done] returns a Future on cab
   */
  def cabAggregate(taxi: Taxi): Future[Done] = Future {
    println("Driver Details:: " + taxi.chauffeurId)
    locations.put(taxi.chauffeurId, taxi)
    Done
  }

  /**
   * This method searches for a cab and returns its GPS location
   * Slick Prepared statement could be used instead of JDBC PS. Slick PS is under construction.
   * @param lat latitude of User
   * @param lng longitude of User
   * @param radiuAsList  distance within which Cabs to be searched in KM
   * @return Future[Drivers] returns a Future on cab
   */
  def searchCabs(lat: Float, lng: Float, radiuAsList: List[String], limit: Int): Future[Drivers] = Future {

    val rad = radiuAsList.toList match {
      case Nil => println(s"No Radius Param: Defaulting to 500")
        500
      case radius :: Nil => println(s"Just one  Radius Param.  Just Taking that !!!!")
        radius.toLong
      case multipleRadius => println(s"Multiple radius in Param. Just Taking one !!!!")
        multipleRadius(0).toLong
    }
    val distInKM: Float = rad.toFloat / 1000
    println("Searching for Cabs within " + distInKM + " KM around You !!!!:: ")
    new Drivers(DataAccessLayer.getMyDrivers(lat,lng, distInKM, limit))
  }

  /**
   * This method searches for a cab and returns its GPS location
   * @param id Cab resource to add
   * @return Future[Option[Cab]] returns a Future on cab
   */

  def searchCab(id: Long): Future[Option[Taxi]] = Future {
    println("Searching for Cabs:: " + id)
   /* Some(locations.getOrElse[Taxi](id, {
      new Taxi(1, 1, 1, 222)
    }))*/
    Some(DataAccessLayer.getDriverById(id.toInt))
  }

  /**
   * This method searches for a cab and returns its GPS location.
   * This api use TypeSafe Slick API for DB update
   * @param id Cab resource to add
   * @param taxi Taxi details to be updated
   * @return Future[Option[Cab]] returns a Future on cab
   */
  def updateCab(id: Long, taxi: Taxi): Future[Done] = Future {
    println("Updating Cab Location :: " + id)
    Await.result(db.run(updateLocation(taxi.lattitude, taxi.longitude, taxi.chauffeurId.toInt)).map { res => println(res) }, Duration.Inf)
    Done
  }

}
