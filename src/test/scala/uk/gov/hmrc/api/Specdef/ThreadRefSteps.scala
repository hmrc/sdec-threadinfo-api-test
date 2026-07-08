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

package uk.gov.hmrc.api.Specdef

import org.scalatest.matchers.must.Matchers.include
import org.scalatest.matchers.should.Matchers.{convertToStringShouldWrapperForVerb, shouldBe}
import uk.gov.hmrc.api.client.TestClient

import java.net.URI
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.net.http.{HttpClient, HttpRequest, HttpResponse}

trait ThreadRefSteps {

  protected def loadResource(path: String): String = {
    val uri = getClass.getResource(path).toURI
    new String(Files.readAllBytes(Paths.get(uri)), StandardCharsets.UTF_8)
  }

  protected def normalizeJsonString(json: String): String =
    json.replaceAll("\\s+", "").trim

  protected def normalizeResponseJson(response: String): String = {
    val trimmed   = response.trim
    val unquoted  = if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
      trimmed.substring(1, trimmed.length - 1)
    } else trimmed
    val unwrapped = if (unquoted.startsWith("[") && unquoted.endsWith("]")) {
      unquoted.substring(1, unquoted.length - 1)
    } else unquoted
    normalizeJsonString(unwrapped)
  }

  protected def stripDynamicTimestamps(json: String): String =
    json
      .replaceAll("""\"createdTimeStamp\"\s*:\s*\"[^\"]*\",?""", "")
      .replaceAll("""\"lastUpdatedTimeStamp\"\s*:\s*\"[^\"]*\",?""", "")
      .replaceAll(""",\s*\}""", "}")
      .replaceAll("""\{,""", "{")
      .trim

  protected def expectedThreadReferenceJson(): String =
    loadResource("/jsonSchema/Response/threadReferenceExpected.json")

  protected def sendThreadReferenceRequest(threadId: String): HttpResponse[String] = {
    // val requestUrl = s"http://localhost:4001/sdec-threadinfo-api/thread-reference/$threadId"
    val requestUrl = TestClient.threadReferenceUrl(threadId)
    val client     = HttpClient.newHttpClient()
    val request    = HttpRequest.newBuilder(URI.create(requestUrl)).GET().build()
    client.send(request, HttpResponse.BodyHandlers.ofString())
  }

  protected def normalizeThreadReferenceResponse(responseBody: String): String =
    stripDynamicTimestamps(normalizeResponseJson(responseBody))

}
