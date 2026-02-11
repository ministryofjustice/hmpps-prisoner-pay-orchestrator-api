package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.dto.PayRateDto
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.PENTONVILLE
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.UUID2
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.payRate
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.payStatusPeriod
import java.time.Instant
import java.time.LocalDate

class PayRateIntegrationTest : IntegrationTestBase() {
  private lateinit var today: LocalDate

  @BeforeEach
  fun clockSetup() {
    whenever(clock.instant()).thenReturn(Instant.parse("2026-02-01T00:00:00.00Z"))
    today = LocalDate.now(clock)
  }

  @Test
  fun `should retrieve pay rates with prisoner counts`() {
    val payRates = listOf(
      payRate(
        startDate = today.minusDays(20),
      ),
      payRate(
        id = UUID2,
        startDate = today.plusDays(20),
      ),
    )

    val payStatusPeriods = listOf(
      payStatusPeriod(startDate = today.minusDays(20)),
      payStatusPeriod(startDate = today.minusDays(20)),
    )

    prisonPayApi().apply {
      stubGetPrisonPayRates(PENTONVILLE, payRates)
      stubSearch(PENTONVILLE, today, true, payStatusPeriods)
    }

    val result = getPrisonPayRates(PENTONVILLE).successList<PayRateDto>()

    val pastRates = result.filter { it.startDate <= today }
    val futureRates = result.filter { it.startDate > today }

    assertThat(pastRates).hasSize(1)
    assertThat(pastRates[0].prisonerCount).isEqualTo(payStatusPeriods.size)

    assertThat(futureRates).hasSize(1)
    assertThat(futureRates[0].prisonerCount).isEqualTo(0)
  }

  @Test
  fun `should return zero prisoner count when no active prisoners exists`() {
    val payRates = listOf(
      payRate(
        startDate = today.minusDays(20),
      ),
      payRate(
        id = UUID2,
        startDate = today.plusDays(20),
      ),
    )

    prisonPayApi().apply {
      stubGetPrisonPayRates(PENTONVILLE, payRates)
      stubSearch(PENTONVILLE, today, true, emptyList())
    }

    val result = getPrisonPayRates(PENTONVILLE).successList<PayRateDto>()

    assertThat(result[0].prisonerCount).isEqualTo(0)
    assertThat(result[1].prisonerCount).isEqualTo(0)
  }

  @Test
  fun `should return empty list when no pay rates exist`() {
    prisonPayApi().apply {
      stubGetPrisonPayRates(PENTONVILLE, emptyList())
      stubSearch(PENTONVILLE, today, true, emptyList())
    }

    val result = getPrisonPayRates(PENTONVILLE).successList<PayRateDto>()
    assertThat(result).isEmpty()
  }

  @Test
  fun `should return unauthorized when no bearer token`() {
    getPrisonPayRates(PENTONVILLE, includeBearerAuth = false).fail(HttpStatus.UNAUTHORIZED)
  }

  @Test
  fun `should return forbidden when role is incorrect`() {
    getPrisonPayRates(PENTONVILLE, roles = listOf("ROLE_TEST")).fail(HttpStatus.FORBIDDEN)
  }

  private fun getPrisonPayRates(
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
