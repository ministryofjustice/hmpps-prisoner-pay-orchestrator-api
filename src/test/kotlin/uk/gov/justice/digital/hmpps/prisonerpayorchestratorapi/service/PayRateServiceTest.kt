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
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.clock
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.payRate
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.payStatusPeriod
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.mapping.toModel
import java.time.LocalDate

class PayRateServiceTest {
  private val prisonerPayApiClient: PrisonerPayApiClient = mock()
  private val payRateService = PayRateService(prisonerPayApiClient, clock)
  val today: LocalDate = LocalDate.now(clock)

  @Test
  fun `should return pay rates with correct prisoner counts`() = runTest {
    val payRates = listOf(
      payRate(
        id = UUID1,
        startDate = today.minusDays(10),
      ),
      payRate(
        id = UUID2,
        startDate = today.plusDays(25),
      ),
    )

    val payStatusPeriods = listOf(
      payStatusPeriod(startDate = today.minusDays(10)),
      payStatusPeriod(startDate = today.minusDays(10)),
    )

    prisonerPayApiClient.apply {
      whenever(getPrisonPayRates(PENTONVILLE)).thenReturn(payRates)
      whenever(search(PENTONVILLE, LocalDate.now(clock), true))
        .thenReturn(payStatusPeriods)
    }

    payRateService.getPrisonPayRates(PENTONVILLE).let { result ->
      assertThat(result).hasSize(2)
      assertThat(result).isEqualTo(
        payRates.map { rate ->
          rate.toModel(
            prisonerCount = payStatusPeriods.count { it.type == rate.type && it.startDate == rate.startDate },
          )
        },
      )
    }
  }

  @Test
  fun `should return empty list when no pay rates exist`() = runTest {
    prisonerPayApiClient.apply {
      whenever(getPrisonPayRates(PENTONVILLE)).thenReturn(emptyList())
      whenever(search(PENTONVILLE, LocalDate.now(clock), true))
        .thenReturn(emptyList())
    }

    val result = payRateService.getPrisonPayRates(PENTONVILLE)
    assertThat(result).isEmpty()
  }

  @Test
  fun `should return pay rates with zero prisoner count when no pay status periods exist`() = runTest {
    val payRates = listOf(
      payRate(id = UUID1),
      payRate(id = UUID2),
    )

    prisonerPayApiClient.apply {
      whenever(getPrisonPayRates(PENTONVILLE)).thenReturn(payRates)
      whenever(search(PENTONVILLE, LocalDate.now(clock), true))
        .thenReturn(emptyList())
    }

    val result = payRateService.getPrisonPayRates(PENTONVILLE)
    assertThat(result).allMatch { it.prisonerCount == 0 }
  }

  @Test
  fun `should return prisoner count as zero for future pay rates`() = runTest {
    val payRates = listOf(
      payRate(id = UUID1, startDate = today.plusDays(10)),
      payRate(id = UUID1, startDate = today.plusDays(20)),
    )

    val payStatusPeriods = listOf(
      payStatusPeriod(startDate = today),
    )

    prisonerPayApiClient.apply {
      whenever(getPrisonPayRates(PENTONVILLE)).thenReturn(payRates)
      whenever(search(PENTONVILLE, LocalDate.now(clock), true))
        .thenReturn(payStatusPeriods)
    }

    val result = payRateService.getPrisonPayRates(PENTONVILLE)
    assertThat(result).allMatch { it.prisonerCount == 0 }
  }
}
