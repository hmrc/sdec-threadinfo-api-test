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
    semanticdbEnabled := true,
    semanticdbEnabled := true,
    Test / fork := true,
    Test / javaOptions ++= Seq(
      s"-Dbrowser=${sys.props.getOrElse("browser", "chrome")}",
      s"-Denvironment=${sys.props.getOrElse("environment", "local")}",
      s"-Dbrowser.option.headless=${sys.props.getOrElse("browser.option.headless", "true")}",
      s"-Dbrowser.usePreviousVersion=${sys.props.getOrElse("browser.usePreviousVersion", "true")}"
    ),
    Test / parallelExecution := false
  )

addCommandAlias("prePrChecks", "; scalafmtCheckAll; scalafmtSbtCheck; scalafixAll --check")
addCommandAlias("lint", "; scalafmtAll; scalafmtSbt; scalafixAll")
addCommandAlias("prePush", "; reload; clean; compile; test; lint;")
