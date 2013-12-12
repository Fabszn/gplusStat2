name := "gplusStat2"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.google.apis" % "google-api-services-plus" % "v1-rev99-1.17.0-rc",
  "com.google.api-client" % "google-api-client" % "1.17.0-rc",
  "com.google.http-client" % "google-http-client-jackson" % "1.17.0-rc",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.0-SNAPSHOT",
  "org.jsoup" % "jsoup" % "1.7.2",
  "org.scalatest" % "scalatest_2.10" % "2.0" % "test"
)

resolvers += "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

play.Project.playScalaSettings
