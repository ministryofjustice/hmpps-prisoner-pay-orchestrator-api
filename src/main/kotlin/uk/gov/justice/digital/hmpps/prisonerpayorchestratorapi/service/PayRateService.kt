package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PrisonerPayApiClient
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.dto.PayRateDto
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.mapping.toModel

@Service
class PayRateService(
  private val prisonerPayApiClient: PrisonerPayApiClient,
) {
  suspend fun getCurrentAndFuturePayRates(prisonCode: String): List<PayRateDto> = prisonerPayApiClient.getCurrentAndFuturePayRates(prisonCode).map { it.toModel() }
}
