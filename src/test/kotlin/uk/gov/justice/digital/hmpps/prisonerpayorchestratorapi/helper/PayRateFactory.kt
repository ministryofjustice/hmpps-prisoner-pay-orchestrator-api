package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper

import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PayRate
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PayStatusType
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

internal fun payRate(
  id: UUID = UUID1,
  prisonCode: String = PENTONVILLE,
  type: PayStatusType = PayStatusType.LONG_TERM_SICK,
  startDate: LocalDate = LocalDate.of(2026, 1, 1),
  rate: Int = 99,
  createdBy: String = "TEST_USER",
  createdDateTime: LocalDateTime = LocalDateTime.of(2026, 1, 1, 10, 0),
) = PayRate(
  id = id,
  prisonCode = prisonCode,
  type = type,
  startDate = startDate,
  rate = rate,
  createdBy = createdBy,
  createdDateTime = createdDateTime,
)
