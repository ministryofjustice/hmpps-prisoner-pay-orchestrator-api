package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.service

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PagedPrisoner
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PrisonerSearchClient
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.dto.Prisoner
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.Prisoner as PrisonSearchPrisoner

class CandidateServiceTest {
  val prisonerSearchClient: PrisonerSearchClient = mock()

  val candidateService = CandidateService(prisonerSearchClient)

  @Test
  fun `should return candidates`() = runTest {
    val prisoners = listOf(
      PrisonSearchPrisoner(
        prisonerNumber = "A1111AA",
        firstName = "JOHN",
        lastName = "SMITH",
        cellLocation = "A-1-001",
      ),
      PrisonSearchPrisoner(
        prisonerNumber = "B2222BB",
        firstName = "ALAN",
        lastName = "JOSEPH",
        cellLocation = "B-2-002",
      ),
    )

    whenever(prisonerSearchClient.search("RSI", "Blo")).thenReturn(PagedPrisoner(prisoners))

    val result = candidateService.search("RSI", "Blo")

    assertThat(result).isEqualTo(
      listOf(
        Prisoner(
          prisonerNumber = "A1111AA",
          firstName = "JOHN",
          lastName = "SMITH",
          cellLocation = "A-1-001",
        ),
        Prisoner(
          prisonerNumber = "B2222BB",
          firstName = "ALAN",
          lastName = "JOSEPH",
          cellLocation = "B-2-002",
        ),
      ),
    )
  }
}
