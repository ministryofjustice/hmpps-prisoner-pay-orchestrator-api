package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PayStatusType
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.PENTONVILLE
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.payStatusPeriod
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.prisoner
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.today
import java.time.LocalDate
import java.time.LocalDateTime
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.dto.PayStatusPeriod as PayStatusPeriodDto

class PayStatusPeriodIntegrationTest : IntegrationTestBase() {

  @Test
  fun `search for pay status periods`() {
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

    val prisonerNumbers = setOf("A1111AA", "B2222BB", "C3333CC")

    prisonPayApi().stubSearch(
      prisonCode = PENTONVILLE,
      latestStartDate = latestStartDate,
      activeOnly = false,
      response = payStatusPeriods,
    )

    prisonSearchApi().stubSearchByPrisonerNumbers(prisonerNumbers, prisoners)

    val result = searchPayStatusPeriods(latestStartDate, false, PENTONVILLE).successList<PayStatusPeriodDto>()

    assertThat(result).isEqualTo(
      listOf(
        PayStatusPeriodDto(
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
        PayStatusPeriodDto(
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

  @Test
  fun `search returns forbidden when no bearer token`() {
    searchPayStatusPeriods(includeBearerAuth = false).fail(HttpStatus.UNAUTHORIZED)
  }

  private fun searchPayStatusPeriods(
    latestStartDate: LocalDate = today(),
    activeOnly: Boolean = true,
    prisonCode: String = PENTONVILLE,
    username: String = USERNAME,
    roles: List<String> = listOf(),
    includeBearerAuth: Boolean = true,
  ) = webTestClient
    .get()
    .uri { uriBuilder ->
      uriBuilder
        .path("/pay-status-periods")
        .queryParam("prisonCode", prisonCode)
        .queryParam("latestStartDate", latestStartDate)
        .queryParam("activeOnly", activeOnly)
        .build()
    }
    .accept(MediaType.APPLICATION_JSON)
    .headers(if (includeBearerAuth) setAuthorisation() else noAuthorisation())
    .exchange()
}
