package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.service

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PrisonerPayApiClient
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.PENTONVILLE
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.UUID2
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.clock
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.payRate
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.payStatusPeriod
import java.time.LocalDate

class PayRateServiceTest {
  private val prisonerPayApiClient: PrisonerPayApiClient = mock()
  private val payRateService = PayRateService(prisonerPayApiClient, clock)
  val today: LocalDate = LocalDate.now(clock)

  @Test
  fun `should return pay rates with correct prisoner counts`() = runTest {
    val payRates = listOf(
      payRate(
        startDate = today.minusDays(10),
      ),
      payRate(
        id = UUID2,
        startDate = today.plusDays(10),
      ),
    )

    val payStatusPeriods = listOf(
      payStatusPeriod(),
      payStatusPeriod(startDate = today.minusDays(10)),
    )

    prisonerPayApiClient.apply {
      whenever(getPayRates(PENTONVILLE)).thenReturn(payRates)
      whenever(search(PENTONVILLE, today, true))
        .thenReturn(payStatusPeriods)
    }

    val result = payRateService.getPrisonPayRates(PENTONVILLE)

    assertThat(result[0].prisonerCount).isEqualTo(payStatusPeriods.size)
    assertThat(result[1].prisonerCount).isEqualTo(0)
  }

  @Test
  fun `should return empty list when no pay rates exist`() = runTest {
    prisonerPayApiClient.apply {
      whenever(getPayRates(PENTONVILLE)).thenReturn(emptyList())
      whenever(search(PENTONVILLE, today, true))
        .thenReturn(emptyList())
    }

    val result = payRateService.getPrisonPayRates(PENTONVILLE)
    assertThat(result).isEmpty()
  }

  @Test
  fun `should return pay rates with zero prisoner count when no pay status periods exist`() = runTest {
    val payRates = listOf(payRate())

    prisonerPayApiClient.apply {
      whenever(getPayRates(PENTONVILLE)).thenReturn(payRates)
      whenever(search(PENTONVILLE, today, true))
        .thenReturn(emptyList())
    }

    val result = payRateService.getPrisonPayRates(PENTONVILLE)
    assertThat(result[0].prisonerCount).isEqualTo(0)
  }
}
