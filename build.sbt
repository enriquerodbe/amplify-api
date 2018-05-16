name := "amplify-api"
version := sys.props.getOrElse("amplify.api.version", "1.0-SNAPSHOT")
scalaVersion := "2.12.4"

lazy val dependencies = Seq(
  guice,
  ws,
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.3",
  "com.github.dnvriend" %% "akka-persistence-jdbc" % "3.3.0",
  "org.postgresql" % "postgresql" % "42.2.2",
  "be.objectify" %% "deadbolt-scala" % "2.6.1",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % "it,test",
  "org.mockito" % "mockito-core" % "2.13.0" % "it,test")

lazy val customDependencyOverrides = Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  "com.typesafe.akka" %% "akka-actor" % "2.5.11",
  "com.google.guava" % "guava" % "22.0"
)
dependencyOverrides ++= customDependencyOverrides

lazy val `amplify-api` = (project in file("."))
  .settings(libraryDependencies ++= dependencies)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(scalaSource in IntegrationTest := baseDirectory.value / "it")
  .enablePlugins(PlayScala, SwaggerPlugin)

// Swagger
swaggerDomainNameSpaces := Seq("com.amplify.api.controllers.dtos")

// Integration tests
fork in IntegrationTest := true
javaOptions in IntegrationTest += "-Dconfig.file=it/conf/application.test.conf"

// Scoverage
coverageMinimum := 71
coverageFailOnMinimum := true
lazy val coverageExcludedPackagesSeq = Seq(
  "<empty>",
  "Reverse.*",
  "router",
  "users",
  "venues\\..*",
  "com.amplify.api.aggregates.queue.serialization")
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
  Seq(file("app/com/amplify/api/aggregates/queue/serialization/protobuf"))
