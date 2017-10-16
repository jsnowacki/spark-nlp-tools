name := "spark-nlp-tools"

version := "0.1"

scalaVersion := "2.11.11"

val sparkVersion = "2.2.0"

javacOptions ++= Seq("-encoding", "UTF-8")
javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

// Note the dependencies are provided
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "compile",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "compile",
  "com.optimaize.languagedetector" % "language-detector" % "0.6"
)

// Do not include Scala in the assembled JAR
assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

assemblyMergeStrategy in assembly := {
//  case PathList("org", "apache", "spark", xs @ _*) => MergeStrategy.discard
  case PathList("org",   "apache", xs @ _*) => MergeStrategy.last
  case PathList("com",   "google", xs @ _*) => MergeStrategy.last
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case "about.html" => MergeStrategy.rename
  case "plugin.properties" => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.last
  case "plugin.xml" => MergeStrategy.last
  case "overview.html" => MergeStrategy.last
  case "parquet.thrift" => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}