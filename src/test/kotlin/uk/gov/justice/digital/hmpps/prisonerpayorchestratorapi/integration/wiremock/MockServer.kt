package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.integration.wiremock

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock

abstract class MockServer(port: Int, private val urlPrefix: String = "") : WireMockServer(port) {

  protected val mapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

  fun stubHealthPing(status: Int) {
    stubFor(
      WireMock.get("$urlPrefix/health/ping").willReturn(
        WireMock.aResponse()
          .withHeader("Content-Type", "application/json")
          .withBody(if (status == 200) """{"status":"UP"}""" else """{"status":"DOWN"}""")
          .withStatus(status),
      ),
    )
  }
}
