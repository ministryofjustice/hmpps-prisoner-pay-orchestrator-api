package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.dto.PayRateDto
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.RISLEY_PRISON_CODE
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.UUID1
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.UUID2
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.payRate
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.mapping.toModel
import java.time.LocalDate

class PayRateIntegrationTest : IntegrationTestBase() {
  @Test
  fun `should retrieve pay rates`() {
    val payRates = listOf(
      payRate(
        id = UUID1,
        startDate = LocalDate.of(2026, 1, 20),
        rate = 100,
      ),
      payRate(
        id = UUID2,
        startDate = LocalDate.of(2026, 5, 1),
        rate = 80,
      ),
    )

    prisonPayApi().stubGetCurrentAndFuturePayRate(RISLEY_PRISON_CODE, payRates)
    val result = getCurrentAndFuturePayRates(RISLEY_PRISON_CODE).successList<PayRateDto>()

    val expected = payRates.map { it.toModel() }
    assertThat(result).isEqualTo(expected)
  }

  @Test
  fun `should return empty list when no current or future long term sick pay rates exist`() {
    prisonPayApi().stubGetCurrentAndFuturePayRate(RISLEY_PRISON_CODE, emptyList())
    assertThat(getCurrentAndFuturePayRates(RISLEY_PRISON_CODE).successList<PayRateDto>()).isEmpty()
  }

  @Test
  fun `getPayRates returns unauthorized when no bearer token`() {
    getCurrentAndFuturePayRates(RISLEY_PRISON_CODE, includeBearerAuth = false).fail(HttpStatus.UNAUTHORIZED)
  }

  @Test
  fun `getPayRates returns forbidden when role is incorrect`() {
    getCurrentAndFuturePayRates(RISLEY_PRISON_CODE, roles = listOf("ROLE_TEST")).fail(HttpStatus.FORBIDDEN)
  }

  private fun getCurrentAndFuturePayRates(
    prisonCode: String,
    roles: List<String> = listOf("ROLE_PRISONER_PAY__PRISONER_PAY_UI"),
    includeBearerAuth: Boolean = true,
  ) = webTestClient
    .get()
    .uri { uriBuilder ->
      uriBuilder
        .path("/pay-rates/prison/{prisonCode}")
        .build(prisonCode)
    }
    .accept(MediaType.APPLICATION_JSON)
    .headers(if (includeBearerAuth) setAuthorisation(roles = roles) else noAuthorisation())
    .exchange()
}
