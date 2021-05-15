import Dependencies._
enablePlugins(JavaAppPackaging)
enablePlugins(LinuxPlugin)

logLevel := Level.Error

trapExit := false
compileOrder := CompileOrder.JavaThenScala

val sparkVersion = "2.4.4"
//val sparkVersion = "2.4.4.0-mapr-630"

lazy val root = (project in file(".")).
  settings(
    version := "1.0.0",
    organization := "com.cze",
    scalaVersion := "2.11.12",
    name := "generic",
    resolvers ++= Seq(
      "Spray Repository" at "https://repo.spray.io",
      "MapR Repository" at "https://repository.mapr.com/maven/",
      "xerces-resolver" at "https://repo.jfrog.org/artifactory/libs-releases/",
      "xerces-resolver2" at "https://repository.jboss.org/nexus/content/groups/public-jboss/",
      "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"
      //"SparkPackages" at "https://dl.bintray.com/spark-packages/maven",
    ),
    libraryDependencies ++= Seq(
      //change all spark's to provided whenever build spark-submit
      "org.apache.spark" % "spark-core_2.11" % sparkVersion,
      "org.apache.spark" % "spark-sql_2.11" % sparkVersion,
      "org.apache.spark" % "spark-mllib_2.11" % sparkVersion,
      "org.apache.spark" % "spark-streaming_2.11" % sparkVersion,
      "org.apache.spark" % "spark-yarn_2.11" % sparkVersion,
      "org.apache.spark" % "spark-sql-kafka-0-10_2.11" % sparkVersion,
      "com.microsoft.azure" % "spark-mssql-connector_2.11_2.4" % "1.0.2",
      "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.8",
      "com.typesafe.play" %% "play" % "2.6.11",
      "io.netty" % "netty-all" % "4.1.17.Final",
      "com.typesafe" % "config" % "1.3.2",
      "io.netty" % "netty-all" % "4.1.48.Final",
      "xerces" % "xercesImpl" % "2.11.0.SP5"
    ).map(_.exclude("org.slf4j", "log4j-over-slf4j")),
    mainClass in(Compile, run) := Some("com.hpe.generic.Main"),
    mainClass in(Compile, packageBin) := Some("com.hpe.generic.Main"),
    dependencyOverrides ++= Seq(
    ),
    unmanagedBase := baseDirectory.value / "lib"
  )

val meta = """META.INF(.)*""".r
assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
  case n if n.startsWith("reference.conf") => MergeStrategy.concat
  case n if n.endsWith(".conf") => MergeStrategy.concat
  case meta(_) => MergeStrategy.discard
  case x => MergeStrategy.first
}