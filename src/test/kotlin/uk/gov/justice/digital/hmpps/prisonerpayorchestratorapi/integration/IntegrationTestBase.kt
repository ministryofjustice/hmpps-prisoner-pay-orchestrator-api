package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.integration

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.integration.wiremock.HmppsAuthApiExtension
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.integration.wiremock.HmppsAuthApiExtension.Companion.hmppsAuth
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.integration.wiremock.PrisonerPayApiExtension
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.integration.wiremock.PrisonerSearchExtension
import uk.gov.justice.hmpps.test.kotlin.auth.JwtAuthorisationHelper

internal const val USERNAME = "TestUser"

@ExtendWith(HmppsAuthApiExtension::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(
  HmppsAuthApiExtension::class,
  PrisonerPayApiExtension::class,
  PrisonerSearchExtension::class,
)
@AutoConfigureWebTestClient
abstract class IntegrationTestBase {

  @Autowired
  protected lateinit var webTestClient: WebTestClient

  @Autowired
  protected lateinit var jwtAuthHelper: JwtAuthorisationHelper

  protected fun prisonPayApi() = PrisonerPayApiExtension.server
  protected fun prisonSearchApi() = PrisonerSearchExtension.server

  internal fun setAuthorisation(
    username: String? = "AUTH_ADM",
    roles: List<String> = listOf(),
    scopes: List<String> = listOf("read"),
  ): (HttpHeaders) -> Unit = jwtAuthHelper.setAuthorisationHeader(username = username, scope = scopes, roles = roles)

  internal fun noAuthorisation(
    username: String? = USERNAME,
    roles: List<String> = listOf(),
    scopes: List<String> = listOf(),
  ): (HttpHeaders) -> Unit = {
    println("No auth header set")
  }

  protected fun stubPingWithResponse(status: Int) {
    hmppsAuth.stubHealthPing(status)
  }

  internal final inline fun <reified T : Any> WebTestClient.ResponseSpec.success(status: HttpStatus = HttpStatus.OK): T = expectStatus().isEqualTo(status)
    .expectBody(T::class.java)
    .returnResult().responseBody!!

  internal final inline fun <reified T : Any> WebTestClient.ResponseSpec.successList(status: HttpStatus = HttpStatus.OK): List<T> = expectStatus().isEqualTo(status)
    .expectBodyList(T::class.java)
    .returnResult().responseBody!!

  internal final fun WebTestClient.ResponseSpec.fail(status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR) = expectStatus().isEqualTo(status)
}
