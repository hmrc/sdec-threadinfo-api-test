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

import play.api.Logging
import uk.gov.hmrc.api.client.TestClient

import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

trait ThreadRefSteps extends Logging {

  protected def loadResource(path: String): String = {
    val uri = getClass.getResource(path).toURI
    new String(Files.readAllBytes(Paths.get(uri)), StandardCharsets.UTF_8)
  }

  protected def normalizeJsonString(json: String): String =
    json.replaceAll("\\s+", "").trim

  protected def normalizeResponseJson(response: String): String = {
    val trimmed  = response.trim
    val unquoted = if trimmed.startsWith("\"") && trimmed.endsWith("\"") then {
      trimmed.substring(1, trimmed.length - 1)
    } else trimmed
    val unwrapped = if unquoted.startsWith("[") && unquoted.endsWith("]") then {
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

  private val timestampFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
  private val dateFormatter      = DateTimeFormatter.ISO_LOCAL_DATE

  private def renderDynamicExpectedJson(json: String): String = {
    val createdTimestamp = LocalDateTime.now().minusDays(2).format(timestampFormatter)
    val updatedTimestamp = LocalDateTime.now().minusHours(3).format(timestampFormatter)
    val expiryDate       = LocalDate.now().plusDays(28).format(dateFormatter)

    json
      .replace("CREATED_TIMESTAMP", createdTimestamp)
      .replace("LAST_UPDATED_TIMESTAMP", updatedTimestamp)
      .replace("THREAD_EXPIRY_DATE", expiryDate)
  }

  protected def expectedThreadReferenceJson(): String =
    renderDynamicExpectedJson(loadResource("/jsonSchema/Response/threadReferenceExpected.json"))

  protected def sendThreadReferenceRequest(threadId: String, bearerToken: String): HttpResponse[String] = {

    val requestUrl = TestClient.threadReferenceUrl(threadId)
    val client     = HttpClient.newHttpClient()
    val request    = HttpRequest
      .newBuilder(URI.create(requestUrl))
      .header("Authorization", s"Bearer $bearerToken")
      .GET()
      .build()
    client.send(request, HttpResponse.BodyHandlers.ofString())
  }

  protected def normalizeThreadReferenceResponse(responseBody: String): String =
    stripDynamicTimestamps(normalizeResponseJson(responseBody))

}
