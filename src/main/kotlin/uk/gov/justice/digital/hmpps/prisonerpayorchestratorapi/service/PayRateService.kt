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

    val payRates = prisonerPayApiClient.getPrisonPayRates(prisonCode)

    // Retrieve active pay status periods
    val payStatusPeriods = prisonerPayApiClient.search(
      prisonCode = prisonCode,
      latestStartDate = today,
      activeOnly = true,
    )

    // Count prisoners by pay status type + startDate
    val prisonerCountsByTypeAndStartDate = payStatusPeriods
      .groupBy { it.type to it.startDate }
      .mapValues { it.value.size }

    // Merging counts
    return payRates.map { payRate ->
      payRate.toModel(
        prisonerCount = prisonerCountsByTypeAndStartDate[
          payRate.type to payRate.startDate,
        ] ?: 0, // future start dates will get count as 0 until they are active
      )
    }
  }
}
