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

import org.scalatest.featurespec.AnyFeatureSpec
import uk.gov.hmrc.api.Specdef.ThreadRefSteps

import scala.concurrent.Future

class ThreadReferenceSpec extends BaseSpec with ThreadRefSteps {
  val jwtToken: Future[String] = authenticationService.getBearerToken()

  Feature("Thread Reference number Validation") {

    Scenario("Valid Thread Reference number returns 200 response") {

      Given("The user makes a GET api call to the correct endpoint ")

      When("The user queries a 12 digit Thread Reference number")
      val bearerToken =
        authenticationService.getBearerToken().futureValue

      val response = sendThreadReferenceRequest("123456ABCDEF", bearerToken)

      Then("The user should be able to see the expected response")

      println(s"Actual response body for 200 response: ${response.body()}")

      val expectedJson           = expectedThreadReferenceJson()
      val normalizedExpectedJson = stripDynamicTimestamps(normalizeJsonString(expectedJson))
      val normalizedActualJson   = normalizeThreadReferenceResponse(response.body())

      response.statusCode() shouldBe 200
      normalizedActualJson  shouldBe normalizedExpectedJson

    }

    Scenario("Invalid thread reference returns 400 Bad Request") {

      Given("The user makes a GET api call to the correct endpoint")
      When("The user queries an invalid thread reference")
      val bearerToken =
        authenticationService.getBearerToken().futureValue

      val response = sendThreadReferenceRequest("999", bearerToken)

      Then("The user should receive a 400 validation error")
      println(s"Actual response body for 400 response: ${response.body()}")

      response.statusCode()                shouldBe 400
      normalizeResponseJson(response.body()) should contain
      """{"message":"Thread reference [999] must be exactly 12 characters long and contain only A-Z and 0-9"}"""
    }

    Scenario("Non-existent thread reference returns 404 Not Found") {

      Given("The user makes a GET api call to the correct endpoint")
      When("The user queries a thread reference that does not exist")
      val bearerToken =
        authenticationService.getBearerToken().futureValue

      val response = sendThreadReferenceRequest("AAAAAAAAAAAA", bearerToken)

      Then("The user should receive a 404 not found error")
      println(s"Actual response body for 404 response: ${response.body()}")

      response.statusCode()                shouldBe 404
      normalizeResponseJson(response.body()) should contain
      """{"message":"Thread reference [AAAAAAAAAAAA] not found"}"""
    }
  }
}
