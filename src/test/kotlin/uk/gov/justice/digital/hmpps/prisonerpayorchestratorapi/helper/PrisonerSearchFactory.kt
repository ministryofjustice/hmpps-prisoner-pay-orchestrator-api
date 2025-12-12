package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper

import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.Prisoner

internal fun prisoner(
  prisonerNumber: String = "A1111AA",
  firstName: String = "Stan",
  lastName: String = "Smith",
  cellLocation: String? = "A-1-001",
) = Prisoner(
  prisonerNumber = prisonerNumber,
  firstName = firstName,
  lastName = lastName,
  cellLocation = cellLocation,
)
