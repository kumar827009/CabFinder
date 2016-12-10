package com.go.mappings

/**
 * Created by NKumar on 12/10/2016.
 */


import com.go.entity.Driver
import com.typesafe.config.ConfigFactory
import slick.backend.StaticDatabaseConfig
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

// Definition of the SUPPLIERS table
class Suppliers(tag: Tag) extends Table[(Int, String, String, String, String, String)](tag, "SUPPLIERS") {
  def id = column[Int]("SUP_ID", O.PrimaryKey)

  // This is the primary key column
  def name = column[String]("SUP_NAME")

  def street = column[String]("STREET")

  def city = column[String]("CITY")

  def state = column[String]("STATE")

  def zip = column[String]("ZIP")

  // Every table needs a * projection with the same type as the table's type parameter
  def * = (id, name, street, city, state, zip)
}

object Application extends App {

  val suppliers = TableQuery[Suppliers]

  //Choose your flavour. One only. The config string refers
  //to settings in application.conf
  /*
    def forConfig(path : scala.Predef.String, config : com.typesafe.config.Config = { /* compiled code */ },
     driver : java.sql.Driver = { /* compiled code */ }) : JdbcBackend.this.Database = { /* compiled code */ }
     */
  val connfig = ConfigFactory.load()
  val db = Database.forConfig("mySqlDB")
  /*
  Test
   */
  @StaticDatabaseConfig("file:src/main/resources/application.conf#mySqlDB")
  val q = sql"select address from location.markers ".as[String]

  def getAddress(id: Int) = sql"select id,address from location.markers where id =$id".as[Tuple2[Int, String]]

  Await.result(
    db.run(q).map { res =>
      println(res)
    }, Duration.Inf)

  println("------------------")

  Await.result(
    db.run(getAddress(1)).map { res =>
      println(res.foreach(a => println(a._1 + " *** " + a._2)))
    }, Duration.Inf)
  println("------------------")

  def searchDrivers(lat: Double, lng: Double, radius: Long, limit: Int) = sql"SELECT id,lat,lng, ( 3959 * acos( cos( radians($lat) ) * cos( radians( lat ) ) * cos( radians( lng ) - radians($lng) ) + sin( radians($lat) ) * sin( radians( lat ) ) ) ) AS distance FROM markers HAVING distance < $radius ORDER BY distance LIMIT 0 , $limit".as[(Int, Float, Float, Long)]

  def updateAddress(lat: Double, lng: Double, id: Int) = sql"update location.drivers set lat=$lat,lng=$lng where id =$id".asUpdate

  println("--------22----------")

  Await.result(
    db.run(updateAddress(39.386339, -122.085823, 1)).map { res =>
      println(res)
    }, Duration.Inf)

  println("--------22----------")


  println("--------5555----------")
  Await.result(
    db.run(searchDrivers(37, -122, 500, 10)).map { res =>
      println(res.foreach(a => println(a._1 + " *** " + a._2 + " **** " + a._3 + "**** " + a._4)))
    }, Duration.Inf)

  println("--------5555----------")

  Await.result(
    db.run(searchDrivers(37, -122, 500, 10)).map(_.foreach{
      case (id, lat,lng,dist) => new Driver(id,lat, lng, dist)
    })
    , Duration.Inf)


  /*
  Test end
   */
  //val db = Database.forConfig("postgresDB")

  //User the schema definition to generate DROP statement
  val dropCmd = DBIO.seq(suppliers.schema.drop)

  //User the schema definition to generate a CREATE TABLE
  //command, followed by INSERTs
  val setup = DBIO.seq(suppliers.schema.create,
    suppliers +=(101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199"),
    suppliers +=(49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460")
  )
  println("------" + db.run(setup))


  def runQuery = {
    val queryFuture = Future {
      //A very naive query which is the equivalent of SELECT * FROM TABLE
      //and having the FRM map the columns to the params of a partial function
      //
      db.run(suppliers.result).map(_.foreach {

        case (id, name, street, city, state, zip) =>
          println("-----------")
          println(s"${name}: ${street} : ${city}")
      })
    }
    case class Markers(id: Int, name: String, address: String, lat: Float, lng: Float)
    /*    def findById(id:Int):Future[List[Markers]] = {
          db(suppliers.filter(_.id === id).sres
        }*/


    //Everything runs asynchronously. Failure to wait for results
    //usually leads to no results :)
    //NOTE: Await does not block here!
    Await.result(queryFuture, Duration.Inf).andThen {
      case Success(_) =>
        println("success !!!!!")
        db.close() //cleanup DB connection
      case Failure(err) => println(err); println("Oh Noes!") //handy for debugging failure
    }

  }

  def dropDB = {

    //do a drop followed by create
    val dropFuture = Future {
      db.run(dropCmd)
    }

    //Attemp to drop the table, and don't care if it
    //fails (NOT GOOD!)
    Await.result(dropFuture, Duration.Inf).andThen {
      case Success(_) => doSomething
      case Failure(_) => doSomething
    }

  }

  def doSomething = {

    //do a drop followed by create
    val setupFuture = Future {
      db.run(setup)
    }

    //once our DB has finished initializing we are ready to roll !
    //NOTE: Await does not block here!
    Await.result(setupFuture, Duration.Inf).andThen {
      case Success(_) => runQuery
      case Failure(err) => println(err);
    }

    //Printing this just for fun. Keep an eye on your console to see this print
    // before the query results :)
    println("Seeya!")
  }

  dropDB //execution starts HERE. With a DROP.
  doSomething
  runQuery

}