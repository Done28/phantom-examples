/*
 * Copyright 2013-2016 Outworkers, Limited.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Explicit written consent must be obtained from the copyright owner,
 * Outworkers Limited before any redistribution is made.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
import sbt.Keys._
import sbt._
import com.twitter.sbt._

lazy val Versions = new {
  val phantom = "1.28.8"
  val util = "0.18.2"
  val json4s = "3.3.0"
  val scalatest = "2.2.4"
  val shapeless = "2.2.5"
  val thrift = "0.8.0"
  val finagle = "6.35.0"
  val twitterUtil = "6.33.0"
  val scrooge = "4.7.0"
  val scalatra = "2.3.0"
  val play = "2.4.6"
  val scalameter = "0.6"
  val spark = "1.2.0-alpha3"
  val scalacheck = "1.13.0"
  val slf4j = "1.7.21"
  val reactivestreams = "1.0.0"
  val akka = "2.3.14"
  val typesafeConfig = "1.2.1"
}

val RunningUnderCi = Option(System.getenv("CI")).isDefined || Option(System.getenv("TRAVIS")).isDefined
lazy val TravisScala211 = Option(System.getenv("TRAVIS_SCALA_VERSION")).exists(_.contains("2.11"))
val defaultConcurrency = 4

val liftVersion: String => String = {
  s => CrossVersion.partialVersion(s) match {
    case Some((major, minor)) if minor >= 11 => "3.0-RC3"
    case _ => "3.0-M1"
  }
}

val sharedSettings: Seq[Def.Setting[_]] = Defaults.coreDefaultSettings ++ Seq(
  organization := "com.outworkers",
  scalaVersion := "2.11.8",
  crossScalaVersions := Seq("2.10.6", "2.11.8"),
  resolvers ++= Seq(
    "Twitter Repository" at "http://maven.twttr.com",
    Resolver.jcenterRepo,
    Resolver.typesafeRepo("releases"),
    Resolver.sonatypeRepo("releases"),
    Resolver.bintrayRepo("websudos", "oss-releases")
  ),
  scalacOptions in ThisBuild ++= Seq(
    "-language:postfixOps",
    "-language:implicitConversions",
    "-language:reflectiveCalls",
    "-language:higherKinds",
    "-language:existentials",
    "-Yinline-warnings",
    "-Xlint",
    "-deprecation",
    "-feature",
    "-unchecked"
  ),
  logLevel in ThisBuild := Level.Info,
  libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % Versions.logback,
    "org.slf4j" % "log4j-over-slf4j" % Versions.slf4j
  ),
  fork in Test := true,
  javaOptions ++= Seq(
    "-Xmx1G",
    "-Djava.net.preferIPv4Stack=true",
    "-Dio.netty.resourceLeakDetection"
  ),
  //testFrameworks in PerformanceTest := Seq(new TestFramework("org.scalameter.ScalaMeterFramework")),
  //testOptions in Test := Seq(Tests.Filter(x => !performanceFilter(x))),
  //testOptions in PerformanceTest := Seq(Tests.Filter(x => performanceFilter(x))),
  //fork in PerformanceTest := false,
  parallelExecution in ThisBuild := false
) ++ VersionManagement.newSettings ++
  GitProject.gitSettings
}

lazy val examples = (project in file("."))
  .settings(sharedSettings: _*)
  .aggregate(
    basic,
    advanced,
    playExample,
    finatraExample
  )

lazy val basic = (project in file("basic")) 
  .settings(sharedSettings: _*)
  .settings(
    name := "basic",
    moduleName := "phantom-examples-basic",
    libraryDependencies ++= Seq(
      "com.websudos" %% "phantom-dsl" % Versions.phantom,
      "com.outworkers" %% "util-testing" % Versions.util % Test
    )
  )

lazy val advanced = (project in file("advanced"))
  .settings(sharedSettings: _*)
  .settings(
    name := "advanced",
    moduleName := "phantom-examples-advanced",
    libraryDependencies ++= Seq(
      "com.websudos" %% "phantom-dsl" % Versions.phantom,
      "com.websudos" %% "phantom-reactivestreams" % Versions.phantom,
      "com.outworkers" %% "util-testing" % Versions.util % Test
    )
  )

lazy val finatraExample = (project in file("finatra"))
  .settings(sharedSettings: _*)
  .settings(
    name := "play",
    moduleName := "phantom-examples-finatra",
    libraryDependencies ++= Seq(
      "com.websudos" %% "phantom-dsl" % Versions.phantom,
      "com.websudos" %% "phantom-finagle" % Versions.phantom,
      "com.outworkers" %% "util-testing" % Versions.util % Test
    )
  ) 

lazy val playExample = (project in file("play"))
  .settings(sharedSettings: _*)
  .settings(
    name := "play",
    moduleName := "phantom-examples-play",
    libraryDependencies ++= Seq(
    "com.websudos" %% "phantom-dsl" % Versions.phantom,
      "com.typesafe.play" %% "play-json" % Versions.play
    )
  )



