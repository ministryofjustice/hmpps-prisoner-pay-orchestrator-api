package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.integration.wiremock

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.Prisoner
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PrisonerNumbersSearch

class PrisonerSearchMockServer : MockServer(8092) {
  fun stubSearchByPrisonerNumbers(prisonerNumbers: Set<String>, prisoners: List<Prisoner>) {
    stubFor(
      WireMock.post(WireMock.urlEqualTo("/prisoner-search/prisoner-numbers"))
        .withRequestBody(equalToJson(mapper.writeValueAsString(PrisonerNumbersSearch(prisonerNumbers = prisonerNumbers)), true, true))
        .willReturn(
          WireMock.aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(mapper.writeValueAsString(prisoners))
            .withStatus(200),
        ),
    )
  }
}

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
