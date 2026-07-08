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

import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}

trait HttpClientSupport {
  protected val httpClient: HttpClient = HttpClient.newHttpClient()

  protected def buildGetRequest(uri: String): HttpRequest =
    HttpRequest.newBuilder(URI.create(uri)).GET().build()

  protected def sendRequest(request: HttpRequest): HttpResponse[String] =
    httpClient.send(request, HttpResponse.BodyHandlers.ofString())
}
object TestClient extends HttpClientSupport {

  private val baseUrl = "http://localhost:4001"

  def get(uri: String): HttpResponse[String] =
    sendRequest(buildGetRequest(uri))

  def threadReferenceUrl(threadId: String): String =
    s"$baseUrl/sdec-threadinfo-api/thread-reference/$threadId"

  def getThreadReference(threadId: String): HttpResponse[String] =
    get(threadReferenceUrl(threadId))
}
