name := "amplify-api"
version := sys.props.getOrElse("amplify.api.version", "1.0-SNAPSHOT")
scalaVersion := "2.12.8"
maintainer := "enriquerodbe@gmail.com"

lazy val dependencies = Seq(
  guice,
  ws,
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.3",
  "org.postgresql" % "postgresql" % "42.2.5",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % "it,test",
  "org.mockito" % "mockito-core" % "2.23.0" % "it,test")

// Remove eviction warnings
lazy val customDependencyOverrides = Seq(
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.8.11",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.8.11",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.11.3",
  "com.google.guava" % "guava" % "23.6.1-jre",
  "com.typesafe" % "config" % "1.3.3",
  "com.typesafe" %% "ssl-config-core" % "0.3.6",
  "com.typesafe.akka" %% "akka-actor" % "2.5.19",
  "com.typesafe.akka" %% "akka-stream" % "2.5.19",
  "com.typesafe.play" %% "play" % "2.6.21",
  "com.typesafe.play" %% "play-akka-http-server" % "2.6.21",
  "com.typesafe.play" %% "play-json" % "2.6.12",
  "com.typesafe.play" %% "play-logback" % "2.6.21",
  "com.typesafe.play" %% "play-server" % "2.6.21",
  "com.typesafe.play" %% "twirl-api" % "1.3.15",
  "org.reactivestreams" % "reactive-streams" % "1.0.2",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.1",
  "org.slf4j" % "slf4j-api" % "1.7.25")
dependencyOverrides ++= customDependencyOverrides

lazy val `amplify-api` = (project in file("."))
  .settings(libraryDependencies ++= dependencies)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(scalaSource in IntegrationTest := baseDirectory.value / "it")
  .enablePlugins(PlayScala, SwaggerPlugin)

// Swagger
swaggerDomainNameSpaces := Seq("com.amplify.api.shared.controllers.dtos")

// Integration tests
fork in IntegrationTest := true
javaOptions in IntegrationTest += "-Dconfig.file=it/conf/application.test.conf"

// Scoverage
coverageMinimum := 75
coverageFailOnMinimum := true
lazy val coverageExcludedPackagesSeq = Seq(
  "com.amplify.api.domain.models.*",
  "<empty>",
  "Reverse.*",
  "router",
  "coins",
  "venues\\..*")
coverageExcludedPackages := coverageExcludedPackagesSeq.mkString(";")

// Scalastyle
lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")
compileScalastyle := scalastyle.in(Compile).toTask("").value
(compile in Compile) := (compile in Compile).dependsOn(compileScalastyle).value
lazy val testScalastyle = taskKey[Unit]("testScalastyle")
testScalastyle := scalastyle.in(Test).toTask("").value
(test in Test) := (test in Test).dependsOn(testScalastyle).value
scalastyleFailOnError := true

// Dist files
sources in (Compile, doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false
topLevelDirectory := None
