package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.mapping

import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.dto.Prisoner
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.Prisoner as PrisonSearchPrisoner

internal fun PrisonSearchPrisoner.toModel() = Prisoner(
  prisonerNumber = prisonerNumber,
  firstName = firstName,
  lastName = lastName,
  cellLocation = cellLocation,
)
