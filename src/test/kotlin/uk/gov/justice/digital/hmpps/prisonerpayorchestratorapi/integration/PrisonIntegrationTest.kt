package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.PENTONVILLE
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.prisoner
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.dto.Prisoner as PrisonerDto

class PrisonIntegrationTest : IntegrationTestBase() {

  @Test
  fun `search for candidates returns candidates`() {
    val prisoners = listOf(
      prisoner(
        prisonerNumber = "A1111AA",
        firstName = "JOHN",
        lastName = "SMITH",
        cellLocation = "A-1-001",
      ),
      prisoner(
        prisonerNumber = "B2222BB",
        firstName = "ALAN",
        lastName = "JOSPEH",
        cellLocation = "B-2-002",
      ),
    )

    prisonSearchApi().stubSearchBySearchTerm(searchTerm = "Jo", prisoners = prisoners)

    val result = searchForCandidates(searchTerm = "Jo").successList<PrisonerDto>()

    assertThat(result).isEqualTo(
      listOf(
        PrisonerDto(
          prisonerNumber = "A1111AA",
          firstName = "JOHN",
          lastName = "SMITH",
          cellLocation = "A-1-001",
        ),
        PrisonerDto(
          prisonerNumber = "B2222BB",
          firstName = "ALAN",
          lastName = "JOSPEH",
          cellLocation = "B-2-002",
        ),
      ),
    )
  }

  @Test
  fun `search returns unauthorized when no bearer token`() {
    searchForCandidates(includeBearerAuth = false).fail(HttpStatus.UNAUTHORIZED)
  }

  @Test
  fun `search returns forbidden when role is incorrect`() {
    searchForCandidates(roles = listOf("ROLE_BLAH")).fail(HttpStatus.FORBIDDEN)
  }

  private fun searchForCandidates(
    prisonCode: String = PENTONVILLE,
    searchTerm: String = "A",
    username: String = USERNAME,
    roles: List<String> = listOf("ROLE_PRISONER_PAY__PRISONER_PAY_UI"),
    includeBearerAuth: Boolean = true,
  ) = webTestClient
    .get()
    .uri { uriBuilder ->
      uriBuilder
        .path("/prison/$prisonCode/candidate-search")
        .queryParam("searchTerm", searchTerm)
        .build()
    }
    .accept(MediaType.APPLICATION_JSON)
    .headers(if (includeBearerAuth) setAuthorisation(roles = roles) else noAuthorisation())
    .exchange()
}
