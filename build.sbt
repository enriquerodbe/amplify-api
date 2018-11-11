name := "amplify-api"
version := sys.props.getOrElse("amplify.api.version", "1.0-SNAPSHOT")
scalaVersion := "2.12.7"

lazy val dependencies = Seq(
  guice,
  ws,
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.3",
  "com.github.dnvriend" %% "akka-persistence-jdbc" % "3.4.0",
  "org.postgresql" % "postgresql" % "42.2.5",
  "be.objectify" %% "deadbolt-scala" % "2.6.1",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % "it,test",
  "org.mockito" % "mockito-core" % "2.23.0" % "it,test")

// Remove eviction warnings
lazy val customDependencyOverrides = Seq(
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.8.11",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.8.11",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.11.1",
  "com.google.guava" % "guava" % "22.0",
  "com.typesafe" % "config" % "1.3.3",
  "com.typesafe" %% "ssl-config-core" % "0.2.4",
  "com.typesafe.akka" %% "akka-actor" % "2.5.17",
  "com.typesafe.akka" %% "akka-persistence" % "2.5.17",
  "com.typesafe.akka" %% "akka-persistence-query" % "2.5.17",
  "com.typesafe.akka" %% "akka-protobuf" % "2.5.17",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.17",
  "com.typesafe.akka" %% "akka-stream" % "2.5.17",
  "com.typesafe.play" %% "play" % "2.6.20",
  "com.typesafe.play" %% "play-akka-http-server" % "2.6.20",
  "com.typesafe.play" %% "play-json" % "2.6.10",
  "com.typesafe.play" %% "play-logback" % "2.6.20",
  "com.typesafe.play" %% "play-server" % "2.6.20",
  "com.typesafe.play" %% "twirl-api" % "1.3.15",
  "com.typesafe.slick" %% "slick" % "3.2.3",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
  "org.reactivestreams" % "reactive-streams" % "1.0.2",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.1",
  "org.slf4j" % "slf4j-api" % "1.7.25"
)
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
coverageMinimum := 72
coverageFailOnMinimum := true
lazy val coverageExcludedPackagesSeq = Seq(
  "<empty>",
  "Reverse.*",
  "router",
  "coins",
  "venues\\..*",
  "com.amplify.api.domain.queue.serialization")
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

// ScalaPB
PB.targets in Compile := Seq(
  scalapb.gen(flatPackage = true) -> (sourceManaged in Compile).value
)
PB.protoSources in Compile :=
  Seq(file("app/com/amplify/api/domain/queue/serialization/protobuf"))
