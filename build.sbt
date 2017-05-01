name := "amplify-api"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

lazy val dependencies = Seq(
  "com.typesafe.play" %% "play-ws" % "2.5.4",
  "com.typesafe.play" %% "play-slick" % "2.1.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.1.0",
  "com.h2database" % "h2" % "1.4.194",
  "com.github.tototoshi" %% "play-json-naming" % "1.1.0",
  "be.objectify" %% "deadbolt-scala" % "2.5.1",
  "com.iheart" %% "play-swagger" % "0.5.4",
  "org.webjars" % "swagger-ui" % "2.2.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % "it,test")

lazy val root = (project in file("."))
  .settings(libraryDependencies ++= dependencies)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(scalaSource in IntegrationTest := baseDirectory.value / "it")
  .enablePlugins(PlayScala, SwaggerPlugin)

// Swagger
swaggerDomainNameSpaces := Seq("com.amplify.api.controllers.dtos")

// Scoverage
coverageEnabled := true
coverageMinimum := 80
coverageFailOnMinimum := true

// Scalastyle
lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")
compileScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value
(compile in Compile) := (compile in Compile).dependsOn(compileScalastyle).value
lazy val testScalastyle = taskKey[Unit]("testScalastyle")
testScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Test).toTask("").value
(test in Test) := (test in Test).dependsOn(testScalastyle).value
scalastyleFailOnError := true
