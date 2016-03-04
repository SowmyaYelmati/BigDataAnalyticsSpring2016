name := "Lab6"

version := "1.0"

scalaVersion := "2.11.0"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.11" % "1.6.0",
  "org.apache.spark" % "spark-streaming_2.11" % "1.6.0",
  "org.apache.spark" % "spark-streaming-twitter_2.11" % "1.6.0",
  "org.apache.spark" % "spark-mllib_2.11" % "1.6.0",
  "org.apache.commons" % "commons-lang3" % "3.0",
  "org.scalaj" %% "scalaj-http" % "1.1.5",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.3.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.3.0" classifier "models"
)
    