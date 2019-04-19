addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.21")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("com.iheart" %% "sbt-play-swagger" % "0.7.4")

// Remove eviction warnings
lazy val customDependencyOverrides = Seq(
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.9.0",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.9.7",
  "com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-base" % "2.9.6",
  "com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-json-provider" % "2.9.6",
  "com.google.guava" % "guava" % "20.0",
  "com.typesafe" % "config" % "1.3.3",
  "com.typesafe" %% "jse" % "1.2.4",
  "com.typesafe.akka" %% "akka-actor" % "2.5.18",
  "net.java.dev.jna" % "jna" % "5.0.0",
  "org.apache.httpcomponents" % "httpcore" % "4.4.5",
  "org.scala-lang.modules" %% "scala-xml" % "1.1.1",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.1",
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "org.webjars" % "webjars-locator-core" %  "0.36")
dependencyOverrides ++= customDependencyOverrides
