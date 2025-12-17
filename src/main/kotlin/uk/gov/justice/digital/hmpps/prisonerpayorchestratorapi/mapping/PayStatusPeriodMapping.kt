package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.mapping

import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.Prisoner
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.dto.PayStatusPeriod
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PayStatusPeriod as PrisonerPayPayStatusPeriod

internal fun PrisonerPayPayStatusPeriod.toModel(prisoner: Prisoner) = PayStatusPeriod(
  id = id,
  prisonCode = prisonCode,
  prisonerNumber = prisonerNumber,
  firstName = prisoner.firstName,
  lastName = prisoner.lastName,
  cellLocation = prisoner.cellLocation,
  type = type,
  startDate = startDate,
  endDate = endDate,
  createdDateTime = createdDateTime,
  createdBy = createdBy,
)
