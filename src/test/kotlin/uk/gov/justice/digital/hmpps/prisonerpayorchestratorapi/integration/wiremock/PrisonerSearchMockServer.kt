package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.integration.wiremock

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.Prisoner
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PrisonerNumbersSearch
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.PENTONVILLE

class PrisonerSearchMockServer : MockServer(8092) {
  fun stubSearchByPrisonerNumbers(prisonerNumbers: Set<String>, prisoners: List<Prisoner>) {
    stubFor(
      post(urlPathEqualTo("/prisoner-search/prisoner-numbers"))
        .withRequestBody(equalToJson(mapper.writeValueAsString(PrisonerNumbersSearch(prisonerNumbers = prisonerNumbers)), true, true))
        .willReturn(
          WireMock.aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(mapper.writeValueAsString(prisoners))
            .withStatus(200),
        ),
    )
  }

  fun stubSearchBySearchTerm(prisonCode: String = PENTONVILLE, searchTerm: String, prisoners: List<Prisoner>) {
    stubFor(
      get(urlPathEqualTo("/prison/$prisonCode/prisoners"))
        .withQueryParam("term", equalTo(searchTerm))
        .willReturn(
          WireMock.aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(mapper.writeValueAsString(PagedResponse(prisoners)))
            .withStatus(200),
        ),
    )
  }
}

data class PagedResponse<T>(val content: List<T>, val totalElements: Long = content.size.toLong())

class PrisonerSearchExtension :
  BeforeAllCallback,
  AfterAllCallback,
  BeforeEachCallback {
  companion object {
    @JvmField
    val server = PrisonerSearchMockServer()
  }

  override fun beforeAll(context: ExtensionContext) {
    server.start()
  }

  override fun beforeEach(context: ExtensionContext) {
    server.resetAll()
  }

  override fun afterAll(context: ExtensionContext) {
    server.stop()
  }
}
