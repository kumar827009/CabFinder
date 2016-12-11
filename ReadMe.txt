This contains scala Akka-HTTP based HTTP server for Taxi aggregation/Driver Finder

Why Akka ?
---------
The Akka HTTP modules implement a full server- and client-side HTTP stack on top
of akka-actor and akka-stream. It's not a web-framework but rather a more general
toolkit for providing and consuming HTTP-based services.

 a) Akka provides highly concurrent and non-blocking asynchronous processing.
 b) Scales well to multinode cluster.



Configuration
-------------
Software Requirement to Run
---------------------------
1) JRE/JDK : 1.7 or higher version. JRE installed on the Linux/Unix box with JAVA_HHOME set
2) Available HTTP port: Configurable
3) MySql Server ad DB : 5.6 version
4) SOAP UI for testing the service.

Steps
-----
Using GIT-Hub download. Build and run (https://github.com/kumar827009/CabFinder.git)

*****Note: Currently not able to run in Unix-Linux. But runs well in IDE. Facing some issue with akka-configuration and requires time to solve.

1) Main Class - com.go.service.GoHttpServer

2) Make changes in the HTTP host and port in application.conf file

3) Make change in the SQl server host/port.

4) Data Setup:
   a) Create a database and table using the sql script DriversData.sql
   b) Import GoJek-soapui-project.xml in local UI (https://www.soapui.org/) and run the GET PUT POST
   c) run the Apache JMeter LoadTest/SOAP UI LoadTest


