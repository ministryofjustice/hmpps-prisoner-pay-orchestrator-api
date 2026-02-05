package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.service

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PrisonerPayApiClient
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.RISLEY_PRISON_CODE
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.UUID1
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.UUID2
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.payRate
import java.time.LocalDate
import java.time.LocalDateTime

class PayRateServiceTest {
  private val prisonerPayApiClient: PrisonerPayApiClient = mock()
  private val payRateService = PayRateService(prisonerPayApiClient)

  @Test
  fun `should retrieve current and future pay rates`() = runTest {
    val payRates = listOf(
      payRate(
        id = UUID1,
        prisonCode = "RSI",
        startDate = LocalDate.of(2026, 2, 2),
        rate = 100,
        createdDateTime = LocalDateTime.of(2026, 2, 1, 10, 0),
      ),
      payRate(
        id = UUID2,
        prisonCode = "RSI",
        startDate = LocalDate.of(2026, 1, 27),
        rate = 80,
        createdDateTime = LocalDateTime.of(2026, 1, 20, 10, 0),
      ),
    )

    whenever(prisonerPayApiClient.getCurrentAndFuturePayRates(RISLEY_PRISON_CODE))
      .thenReturn(payRates)

    val result = payRateService.getCurrentAndFuturePayRates(RISLEY_PRISON_CODE)

    with(result) {
      assertThat(this).hasSize(2)

      with(this[0]) {
        assertThat(id).isEqualTo(UUID1)
        assertThat(prisonCode).isEqualTo("RSI")
        assertThat(rate).isEqualTo(100)
        assertThat(startDate).isEqualTo(LocalDate.of(2026, 2, 2))
      }

      with(this[1]) {
        assertThat(id).isEqualTo(UUID2)
        assertThat(prisonCode).isEqualTo("RSI")
        assertThat(rate).isEqualTo(80)
        assertThat(startDate).isEqualTo(LocalDate.of(2026, 1, 27))
      }
    }
  }

  @Test
  fun `should return empty list when no pay rates exist`() = runTest {
    whenever(prisonerPayApiClient.getCurrentAndFuturePayRates(RISLEY_PRISON_CODE))
      .thenReturn(emptyList())

    val result = payRateService.getCurrentAndFuturePayRates(RISLEY_PRISON_CODE)
    assertThat(result).isEmpty()
  }
}
