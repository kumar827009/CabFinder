package com.go.route

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

/**
 * Created by NKumar on 12/8/2016.
 */
trait ResourceRouter {
  implicit def executionContext: ExecutionContext

  implicit val system = ActorSystem()
  println(system.settings)
  implicit val materializer = ActorMaterializer()
}
