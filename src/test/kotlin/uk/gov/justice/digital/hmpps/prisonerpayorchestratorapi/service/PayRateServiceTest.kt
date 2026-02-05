package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.service

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PrisonerPayApiClient
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.PENTONVILLE
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.UUID1
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.UUID2
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.payRate
import java.time.LocalDate

class PayRateServiceTest {
  private val prisonerPayApiClient: PrisonerPayApiClient = mock()
  private val payRateService = PayRateService(prisonerPayApiClient)

  @Test
  fun `should retrieve current and future pay rates`() = runTest {
    val payRates = listOf(
      payRate(
        id = UUID1,
        startDate = LocalDate.of(2026, 2, 2),
        rate = 100,
      ),
      payRate(
        id = UUID2,
        startDate = LocalDate.of(2026, 1, 27),
        rate = 80,
      ),
    )

    whenever(prisonerPayApiClient.getPrisonPayRates(PENTONVILLE))
      .thenReturn(payRates)

    val result = payRateService.getPrisonPayRates(PENTONVILLE)
    assertThat(result).usingRecursiveComparison().isEqualTo(payRates)
  }

  @Test
  fun `should return empty list when no pay rates exist`() = runTest {
    whenever(prisonerPayApiClient.getPrisonPayRates(PENTONVILLE))
      .thenReturn(emptyList())

    val result = payRateService.getPrisonPayRates(PENTONVILLE)
    assertThat(result).isEmpty()
  }
}
