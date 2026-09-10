/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.api.specs

import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.api.TestModule
import uk.gov.hmrc.api.client.AuthenticationService
import uk.gov.hmrc.http.client.HttpClientV2

import scala.concurrent.ExecutionContext

trait BaseSpec
    extends AnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with Eventually
    with ScalaFutures
    with BeforeAndAfterEach {

  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(
      timeout = Span(5, Seconds),
      interval = Span(500, Millis)
    )

  implicit lazy val ec: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  lazy val app: Application =
    new GuiceApplicationBuilder()
      .bindings(new TestModule)
      .build()

  lazy val httpClient: HttpClientV2 =
    app.injector.instanceOf[HttpClientV2]

  lazy val authenticationService = new AuthenticationService(httpClient)
}
