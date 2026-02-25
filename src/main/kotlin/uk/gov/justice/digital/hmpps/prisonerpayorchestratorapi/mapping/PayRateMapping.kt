package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.mapping

import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PayRate
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.dto.PayRateDto

internal fun PayRate.toModel(prisonerCount: Int = 0): PayRateDto = PayRateDto(
  id = id,
  prisonCode = prisonCode,
  type = type,
  startDate = startDate,
  rate = rate,
  createdDateTime = createdDateTime,
  createdBy = createdBy,
  updatedDateTime = updatedDateTime,
  updatedBy = updatedBy,
  prisonerCount = prisonerCount,
)
