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

package uk.gov.hmrc.api.client

import play.api.Logging
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.api.config.TestConfiguration
import uk.gov.hmrc.api.models.AuthStubRequest
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.http.client.HttpClientV2

import java.net.URI
import scala.concurrent.{ExecutionContext, Future}

class AuthenticationService(client: HttpClientV2)(using ec: ExecutionContext) extends Logging {

  private val authUrl: String = TestConfiguration.url("authStub")

  def getBearerToken(request: AuthStubRequest = AuthStubRequest()): Future[String] = {
    logger.info(s"Getting Bearer Token")
    given hc: HeaderCarrier = HeaderCarrier()
    client
      .post(URI.create(authUrl).toURL)
      .withBody(Json.toJson(request))
      .execute[HttpResponse]
      .map { response =>
        response.headers
          .find { case (k, _) => k.equalsIgnoreCase("Authorization") }
          .flatMap { case (_, values) => values.headOption }
          .flatMap(_.split(",").find(_.trim.startsWith("Bearer ")))
          .map(_.trim.replace("Bearer ", ""))
          .getOrElse(throw new RuntimeException(s"No Bearer token in auth stub response (status: ${response.status})"))
      }
  }
}
