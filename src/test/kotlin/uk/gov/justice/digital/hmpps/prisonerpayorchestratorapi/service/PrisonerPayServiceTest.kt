package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.service

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PayStatusType
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PrisonerPayApiClient
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PrisonerSearchClient
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.dto.PayStatusPeriod
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.PENTONVILLE
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.UUID1
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.payStatusPeriod
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.prisoner
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.today
import java.time.LocalDateTime

class PrisonerPayServiceTest {
  val prisonerPayApiClient: PrisonerPayApiClient = mock()
  val prisonerSearchClient: PrisonerSearchClient = mock()

  val prisonerPayService = PrisonerPayService(prisonerPayApiClient, prisonerSearchClient)

  @Test
  fun `should return pay the status period by id`() = runTest {
    val payStatusPeriod = payStatusPeriod()
    val prisoner = prisoner()

    whenever(prisonerPayApiClient.getById(UUID1)).thenReturn(payStatusPeriod)

    whenever(prisonerSearchClient.findByPrisonerNumber(payStatusPeriod().prisonerNumber)).thenReturn(prisoner)

    val result = prisonerPayService.getById(UUID1)

    assertThat(result).isEqualTo(
      PayStatusPeriod(
        id = payStatusPeriod.id,
        prisonCode = payStatusPeriod.prisonCode,
        prisonerNumber = payStatusPeriod.prisonerNumber,
        firstName = prisoner.firstName,
        lastName = prisoner.lastName,
        cellLocation = prisoner.cellLocation,
        type = payStatusPeriod.type,
        startDate = payStatusPeriod.startDate,
        endDate = payStatusPeriod.endDate,
        createdBy = payStatusPeriod.createdBy,
        createdDateTime = payStatusPeriod.createdDateTime,
      ),
    )
  }

  @Test
  fun `should return search results`() = runTest {
    val latestStartDate = today()

    val payStatusPeriods = listOf(
      payStatusPeriod(
        prisonerNumber = "A1111AA",
        startDate = today().minusDays(12),
        endDate = today().plusDays(1),
        createdBy = "BLOGGSJ",
        createdDateTime = LocalDateTime.now(),
      ),
      payStatusPeriod(
        prisonerNumber = "B2222BB",
        startDate = today().minusDays(6),
        createdBy = "SMITHJ",
        createdDateTime = LocalDateTime.now().minusHours(2),
      ),
      payStatusPeriod(
        prisonerNumber = "C3333CC",
        startDate = today().minusDays(44),
        createdBy = "SMITHJ",
        createdDateTime = LocalDateTime.now().minusHours(3),
      ),
    )

    val prisoners = listOf(
      prisoner(
        prisonerNumber = "A1111AA",
        firstName = "JOHN",
        lastName = "SMITH",
        cellLocation = "A-1-001",
      ),
      prisoner(
        prisonerNumber = "B2222BB",
        firstName = "JONES",
        lastName = "ALAN",
        cellLocation = "B-2-002",
      ),
    )

    whenever(prisonerPayApiClient.search(PENTONVILLE, latestStartDate, true)).thenReturn(payStatusPeriods)

    whenever(prisonerSearchClient.findByPrisonerNumbers(payStatusPeriods.map { it.prisonerNumber }.toSet())).thenReturn(prisoners)

    val result = prisonerPayService.search(PENTONVILLE, latestStartDate, true)

    assertThat(result).isEqualTo(
      listOf(
        PayStatusPeriod(
          id = payStatusPeriods[0].id,
          prisonCode = PENTONVILLE,
          prisonerNumber = "A1111AA",
          firstName = "JOHN",
          lastName = "SMITH",
          cellLocation = "A-1-001",
          type = PayStatusType.LONG_TERM_SICK,
          startDate = payStatusPeriods[0].startDate,
          endDate = payStatusPeriods[0].endDate,
          createdBy = payStatusPeriods[0].createdBy,
          createdDateTime = payStatusPeriods[0].createdDateTime,
        ),
        PayStatusPeriod(
          id = payStatusPeriods[1].id,
          prisonCode = PENTONVILLE,
          prisonerNumber = "B2222BB",
          firstName = "JONES",
          lastName = "ALAN",
          cellLocation = "B-2-002",
          type = PayStatusType.LONG_TERM_SICK,
          startDate = payStatusPeriods[1].startDate,
          endDate = payStatusPeriods[1].endDate,
          createdBy = payStatusPeriods[1].createdBy,
          createdDateTime = payStatusPeriods[1].createdDateTime,
        ),
      ),
    )
  }
}
