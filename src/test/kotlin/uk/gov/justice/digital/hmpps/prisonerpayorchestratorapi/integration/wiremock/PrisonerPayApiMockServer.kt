package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.integration.wiremock

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PayStatusPeriod
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.PENTONVILLE
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.today
import java.time.LocalDate
import java.util.*

class PrisonerPayAPIMockServer : MockServer(8762) {
  fun stubGetById(id: UUID, response: PayStatusPeriod) {
    stubFor(
      WireMock.get(urlPathEqualTo("/pay-status-periods/$id"))
        .willReturn(
          WireMock.aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(mapper.writeValueAsString(response))
            .withStatus(200),
        ),
    )
  }

  fun stubSearch(
    prisonCode: String = PENTONVILLE,
    latestStartDate: LocalDate = today(),
    activeOnly: Boolean = true,
    response: List<PayStatusPeriod>,
  ) {
    stubFor(
      WireMock.get(urlPathEqualTo("/pay-status-periods"))
        .withQueryParam("prisonCode", equalTo(prisonCode))
        .withQueryParam("latestStartDate", equalTo(latestStartDate.toString()))
        .withQueryParam("activeOnly", equalTo(activeOnly.toString()))
        .willReturn(
          WireMock.aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(mapper.writeValueAsString(response))
            .withStatus(200),
        ),
    )
  }
}

class PrisonerPayApiExtension :
  BeforeAllCallback,
  AfterAllCallback,
  BeforeEachCallback {
  companion object {
    @JvmField
    val server = PrisonerPayAPIMockServer()
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
