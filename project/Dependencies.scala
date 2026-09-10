import sbt.*

object Dependencies {
  private val apiTestRunnerVersion = "0.10.0"
  private val httpVerbsTest        = "15.8.0"
  private val scalaTestPlusVersion = "7.0.2"

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% "api-test-runner"         % apiTestRunnerVersion,
    "uk.gov.hmrc"            %% "http-verbs-test-play-30" % httpVerbsTest,
    "org.scalatestplus.play" %% "scalatestplus-play"      % scalaTestPlusVersion
  )

}
