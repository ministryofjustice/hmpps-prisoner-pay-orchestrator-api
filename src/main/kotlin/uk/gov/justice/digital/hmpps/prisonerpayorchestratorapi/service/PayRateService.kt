package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PrisonerPayApiClient
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.dto.PayRateDto
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.mapping.toModel
import java.time.Clock
import java.time.LocalDate

@Service
class PayRateService(
  private val prisonerPayApiClient: PrisonerPayApiClient,
  private val clock: Clock,
) {
  suspend fun getPrisonPayRates(prisonCode: String): List<PayRateDto> {
    val today = LocalDate.now(clock)

    val payRates = prisonerPayApiClient.getPayRates(prisonCode)

    // Active LTS prisoners as of today (endDate already handled by activeOnly=true)
    val payStatusPeriods = prisonerPayApiClient.search(
      prisonCode = prisonCode,
      latestStartDate = today,
      activeOnly = true,
    )

    // Count active prisoners by pay status type
    val activePrisonerCountByType = payStatusPeriods
      .groupBy { it.type }
      .mapValues { it.value.size }

    return payRates.map { payRate ->
      // Only assign prisonerCount to the current past rate of this type
      val prisonerCount = (payRate.startDate <= today).let { isPastOrCurrent ->
        if (isPastOrCurrent) activePrisonerCountByType[payRate.type] ?: 0 else 0
      }

      payRate.toModel(prisonerCount)
    }
  }
}
