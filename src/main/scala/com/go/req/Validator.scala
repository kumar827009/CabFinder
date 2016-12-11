package com.go.req

/**
 * Created by NKumar on 12/11/2016.
 */
trait Validator {

  def validateRequest(lat: Float, lng: Float, rad: List[String]): Option[String] = if (-90 <= lat && lat <= 90) Some("valid") else Some("invalid")
  def validateDriverId(id:Int):Option[String] = if(id >50000) Some("invalid") else Some("valid")

}