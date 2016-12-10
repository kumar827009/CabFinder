package com.go.config

import  slick.driver.MySQLDriver.api._

/**
 * Created by NKumar on 12/10/2016.
 */
trait DbConfiguration {
  lazy val db = Database.forConfig("mySqlDb")
}
