package com.go.resource

import java.sql.{DriverManager, PreparedStatement, ResultSet}

/**
 * Created by NKumar on 12/10/2016.
 */
object JDBCTest {

  def main(args: Array[String]) {
    val cabFinder = "SELECT id,lat,lng, ( 3959 * acos( cos( radians(?) ) * cos( radians( lat ) ) * cos( radians( lng ) - radians(?) ) + sin( radians(?) ) * sin( radians( lat ) ) ) ) AS distance FROM markers HAVING distance < 500 ORDER BY distance LIMIT 0 , 20;"
    Class.forName("com.mysql.jdbc.Driver");

    val con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Location", "root", "admin")
    val ps: PreparedStatement = con.prepareCall(cabFinder)
    ps.setLong(1, 37)
    ps.setLong(2, -122)
    ps.setLong(3, 37)

    val res: ResultSet = ps.executeQuery()
    while (res.next()) {
      val id = res.getInt(1)
      val lat = res.getFloat(2)
      val lng = res.getFloat(3)
      val loc = res.getLong(4)
      println("Driver Id :" + id + " Latitude :" + lat + "longitude: "+ lng + "Distance: " + loc)
    }

  }


}
