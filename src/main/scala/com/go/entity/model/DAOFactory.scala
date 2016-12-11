package com.go.entity.model

import java.sql.{DriverManager, PreparedStatement, ResultSet}

import com.go.entity.{Driver, Taxi}

import scala.collection.mutable

/**
 * Created by NKumar on 12/11/2016.
 */
trait DAOFactory {
  def getDriverDAO(): Unit = {

  }
}

object DataAccessLayer {
  Class.forName("com.mysql.jdbc.Driver")
  val cabFinderQuery = "SELECT id,lat,lng, ( 6371 * acos( cos( radians(?) ) * cos( radians( lat ) ) * cos( radians( lng ) - radians(?) ) + sin( radians(?) ) * sin( radians( lat ) ) ) ) AS distance FROM drivers HAVING distance < ? ORDER BY distance LIMIT 0 , ?"
  val driverDetailFinderQuery = "select id,lat,lng,accuracy from Drivers where id = ?"
  val con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Location", "root", "admin")

  /**
   * This method searches for a cabs aroud the Lat and Lang passed
   * Slick Prepared statement could be used instead of JDBC PS. Slick PS is under construction.
   * @param lat latitude of User
   * @param lng longitude of User
   * @param rad  distance within which Cabs to be searched in KM
   * @return List[Driver] returns a Future on cab
   */
  def getMyDrivers(lat: Float, lng: Float, rad: Float, limit: Int): List[Driver] = {

    val ps: PreparedStatement = con.prepareCall(cabFinderQuery)
    ps.setFloat(1, lat)
    ps.setFloat(2, lng)
    ps.setFloat(3, lat)
    ps.setFloat(4, rad)
    ps.setInt(5, limit)

    val res: ResultSet = ps.executeQuery()
    val lst: scala.collection.mutable.ListBuffer[Driver] = new mutable.ListBuffer[Driver]()
    while (res.next()) {
      val id = res.getInt(1)
      val lat = res.getFloat(2)
      val lng = res.getFloat(3)
      val dist = res.getFloat(4)
      println("Driver Id :" + id + " Latitude :" + lat + "longitude: " + lng + " Distance: " + dist.toFloat)
      lst += new Driver(id, lat, lng, dist)
    }
    lst.toList
  }

  /**
   * This method searches for a cabs aroud the Lat and Lang passed
   * Slick Prepared statement could be used instead of JDBC PS. Slick PS is under construction.
   * @param id id of the drivedr
   * @return Driver returns a Future on cab
   */
  def getDriverById(id: Int): Taxi = {
    var rs: ResultSet = null
    val ps: PreparedStatement = con.prepareCall(driverDetailFinderQuery)
    ps.setInt(1, id)
    rs = ps.executeQuery
    var taxi: Taxi = new Taxi(1, 1, 1, 1)
    while (rs.next) {
      val id: Int = rs.getInt(1)
      val lat: Float = rs.getFloat(2)
      val lng: Float = rs.getFloat(3)
      val accuracy: Float = rs.getFloat(4)
      System.out.println("Retrieved Drivers Details : " + id + "\t" + lat + "\t" + lng + "\t" + accuracy)
      taxi = new Taxi(lat, lng, accuracy, id)
    }
    taxi
  }
}

