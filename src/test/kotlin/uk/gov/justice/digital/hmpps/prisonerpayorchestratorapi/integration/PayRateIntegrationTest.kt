package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PayStatusType
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.dto.PayRateDto
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.RISLEY_PRISON_CODE
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.UUID1
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.UUID2
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.payRate
import java.time.LocalDate
import java.time.LocalDateTime

class PayRateIntegrationTest : IntegrationTestBase() {
  @Test
  fun `should retrieve pay rates`() {
    val payRates = listOf(
      payRate(
        id = UUID1,
        prisonCode = RISLEY_PRISON_CODE,
        startDate = LocalDate.of(2026, 1, 20),
        rate = 100,
        createdDateTime = LocalDateTime.of(2026, 2, 1, 10, 0),
      ),
      payRate(
        id = UUID2,
        prisonCode = RISLEY_PRISON_CODE,
        startDate = LocalDate.of(2026, 5, 1),
        rate = 80,
        createdDateTime = LocalDateTime.of(2026, 1, 20, 10, 0),
      ),
    )

    prisonPayApi().stubGetCurrentAndFuturePayRate(RISLEY_PRISON_CODE, payRates)
    val result = getCurrentAndFuturePayRates(RISLEY_PRISON_CODE).successList<PayRateDto>()

    assertThat(result).hasSize(2)

    assertThat(result).isEqualTo(
      listOf(
        PayRateDto(
          id = payRates[0].id,
          prisonCode = RISLEY_PRISON_CODE,
          type = PayStatusType.LONG_TERM_SICK,
          startDate = payRates[0].startDate,
          rate = payRates[0].rate,
          createdDateTime = payRates[0].createdDateTime,
          createdBy = payRates[0].createdBy,
        ),
        PayRateDto(
          id = payRates[1].id,
          prisonCode = RISLEY_PRISON_CODE,
          type = PayStatusType.LONG_TERM_SICK,
          startDate = payRates[1].startDate,
          rate = payRates[1].rate,
          createdDateTime = payRates[1].createdDateTime,
          createdBy = payRates[1].createdBy,
        ),
      ),
    )
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
