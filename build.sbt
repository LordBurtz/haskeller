val scala3Version = "3.3.1"
val http4sVersion = "0.23.25"
val circeVersion = "0.14.6"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala3-rest-service",
    version := "0.1.0",
    scalaVersion := scala3Version,
    
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "org.typelevel" %% "cats-effect" % "3.5.3",
      "ch.qos.logback" % "logback-classic" % "1.4.14",
      "org.scalameta" %% "munit" % "0.7.29" % Test
    )
  ) 