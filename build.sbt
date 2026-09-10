lazy val root = (project in file("."))
  .settings(
    name := "sdec-threadinfo-api-test",
    version := "0.1.0",
    scalaVersion := "3.3.7",
    scalacOptions ++= Seq("-feature"),
    libraryDependencies ++= Dependencies.test,
    resolvers += MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2"),
    (Compile / compile) := ((Compile / compile) dependsOn (
      Compile / scalafmtSbtCheck,
      Compile / scalafmtCheckAll
    )).value,
    semanticdbEnabled := true
  )

addCommandAlias("prePrChecks", "; scalafmtCheckAll; scalafmtSbtCheck; scalafixAll --check")
addCommandAlias("lint", "; scalafmtAll; scalafmtSbt; scalafixAll")
addCommandAlias("prePush", "; reload; clean; compile; test; lint;")
