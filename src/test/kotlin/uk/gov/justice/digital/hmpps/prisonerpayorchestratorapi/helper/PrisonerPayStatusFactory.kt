package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper

import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PayStatusPeriod
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PayStatusType
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

internal val UUID1 = UUID.fromString("11111111-1111-1111-1111-111111111111")

internal fun payStatusPeriod(
  id: UUID = UUID1,
  prisonerNumber: String = "A1111AA",
  type: PayStatusType = PayStatusType.LONG_TERM_SICK,
  startDate: LocalDate = today(),
  endDate: LocalDate? = today().plusMonths(1),
  createdBy: String = "BLOGGSJ",
  createdDateTime: LocalDateTime = LocalDateTime.now(),
) = PayStatusPeriod(
  id = id,
  prisonerNumber = prisonerNumber,
  type = type,
  startDate = startDate,
  endDate = endDate,
  createdBy = createdBy,
  createdDateTime = createdDateTime,
)
