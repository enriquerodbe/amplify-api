name := "amplify-api"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.4"

lazy val dependencies = Seq(
  "com.typesafe.play" %% "play-ws" % "2.6.10",
  "com.typesafe.play" %% "play-slick" % "3.0.3",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.3",
  "com.h2database" % "h2" % "1.4.196",
  "be.objectify" %% "deadbolt-scala" % "2.6.1",
  "com.iheart" %% "play-swagger" % "0.7.3",
  "org.webjars" % "swagger-ui" % "3.8.0",
  "com.google.api-client" % "google-api-client" % "1.23.0",
  guice,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % "it,test",
  "org.mockito" % "mockito-core" % "2.13.0" % "it,test")

lazy val `amplify-api` = (project in file("."))
  .settings(libraryDependencies ++= dependencies)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(scalaSource in IntegrationTest := baseDirectory.value / "it")
  .enablePlugins(PlayScala, SwaggerPlugin)

fork in IntegrationTest := true
javaOptions in IntegrationTest += "-Dconfig.file=it/conf/application.test.conf"

// Scoverage
// This makes heroku deploy fail
//coverageEnabled := true
//coverageMinimum := 80
//coverageFailOnMinimum := true

// Swagger
swaggerDomainNameSpaces := Seq("com.amplify.api.controllers.dtos")

// Scalastyle
lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")
compileScalastyle := scalastyle.in(Compile).toTask("").value
(compile in Compile) := (compile in Compile).dependsOn(compileScalastyle).value
lazy val testScalastyle = taskKey[Unit]("testScalastyle")
testScalastyle := scalastyle.in(Test).toTask("").value
(test in Test) := (test in Test).dependsOn(testScalastyle).value
scalastyleFailOnError := true
