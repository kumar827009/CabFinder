package com.go

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.MarshallingDirectives
import akka.stream.ActorMaterializer


/**
 * Created by NKumar on 12/3/2016.
 */
object LocationService extends App {
  /*
  Step 1: Create Akka Actor System
  Akka-http use Actor model to process the request response scenarios i.e conncurrent request will be handled by the Actor system. This must be created as a 1st part.
  */
  implicit val system = ActorSystem.create("gps-locator");

  /*
  Step 2:Create Actor Materiliaze
  Akka HTTP uses akka reactive streams for stream processing on TPC.
  So in a reactive system,we need to specify flow materializer which specifies the how requests/repose flow get processed.
  In akka-http, actors will be used for handling request and response flows. So we use ActorMaterializer here.
  */

  implicit val materilaizer = ActorMaterializer()
  /*
  Step 3: Create API route.
  Route specifies the URI endpoints REST server exposing. It is combination of multiple paths.
  A simple path has the following three parts
  1.Directive/URI
  2.HTTP Method
  3.Response

  in the below example:-
  1. / is the directive
  2.  get is HTTP Method
  3.  Returns “hello world” as the response
  */

}
