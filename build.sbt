name := "gplusStat2"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.google.apis" % "google-api-services-plus" % "v1-rev99-1.17.0-rc",
  "com.google.api-client" % "google-api-client" % "1.17.0-rc",
  "com.google.http-client" % "google-http-client-jackson" % "1.17.0-rc"
)


play.Project.playScalaSettings
