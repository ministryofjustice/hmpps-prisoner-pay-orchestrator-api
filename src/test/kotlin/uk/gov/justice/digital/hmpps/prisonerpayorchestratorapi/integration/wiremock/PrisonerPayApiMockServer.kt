package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.integration.wiremock

import com.github.tomakehurst.wiremock.client.WireMock
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PayStatusPeriod
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.today
import java.time.LocalDate

class PrisonerPayAPIMockServer : MockServer(8762) {
  fun stubSearch(latestStartDate: LocalDate = today(), activeOnly: Boolean = true, response: List<PayStatusPeriod>) {
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/pay-status-periods?latestStartDate=$latestStartDate&activeOnly=$activeOnly"))
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
