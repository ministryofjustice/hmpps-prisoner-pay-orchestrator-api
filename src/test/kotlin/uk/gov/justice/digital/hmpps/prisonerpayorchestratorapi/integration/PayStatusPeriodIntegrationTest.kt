package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PayStatusType
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.PENTONVILLE
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.UUID1
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.payStatusPeriod
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.prisoner
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.today
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.dto.PayStatusPeriod as PayStatusPeriodDto

class PayStatusPeriodIntegrationTest : IntegrationTestBase() {
  @Test
  fun `get by id returns the pay status period`() {
    val payStatusPeriod = payStatusPeriod()
    val prisoner = prisoner()

    prisonPayApi().stubGetById(UUID1, payStatusPeriod)
    prisonSearchApi().stubGetPrisonerById(payStatusPeriod.prisonerNumber, prisoner)

    val result = getById(UUID1).success<PayStatusPeriodDto>()

    assertThat(result).isEqualTo(
      PayStatusPeriodDto(
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
  fun `get by id returns unauthorized when no bearer token`() {
    getById(UUID1, includeBearerAuth = false).fail(HttpStatus.UNAUTHORIZED)
  }

  @Test
  fun `get by id returns forbidden when role is incorrect`() {
    getById(UUID1, roles = listOf("ROLE_BLAH")).fail(HttpStatus.FORBIDDEN)
  }

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
  fun `search for pay status periods filtered by type`() {
    val latestStartDate = today()
    val payStatusPeriods = listOf(
      payStatusPeriod(prisonerNumber = "A1111AA", type = PayStatusType.SHORT_TERM_SICK),
    )
    val prisoners = listOf(
      prisoner(prisonerNumber = "A1111AA", firstName = "JOHN", lastName = "SMITH", cellLocation = "A-1-001"),
    )

    prisonPayApi().stubSearch(
      prisonCode = PENTONVILLE,
      latestStartDate = latestStartDate,
      activeOnly = true,
      type = PayStatusType.SHORT_TERM_SICK,
      response = payStatusPeriods,
    )
    prisonSearchApi().stubSearchByPrisonerNumbers(setOf("A1111AA"), prisoners)

    val result = searchPayStatusPeriods(latestStartDate, true, PENTONVILLE, type = PayStatusType.SHORT_TERM_SICK)
      .successList<PayStatusPeriodDto>()

    assertThat(result).hasSize(1)
    assertThat(result[0].prisonerNumber).isEqualTo("A1111AA")
    assertThat(result[0].type).isEqualTo(PayStatusType.SHORT_TERM_SICK)
  }

  @Test
  fun `search returns unauthorized when no bearer token`() {
    searchPayStatusPeriods(includeBearerAuth = false).fail(HttpStatus.UNAUTHORIZED)
  }

  @Test
  fun `search returns forbidden when role is incorrect`() {
    searchPayStatusPeriods(roles = listOf("ROLE_BLAH")).fail(HttpStatus.FORBIDDEN)
  }

  private fun getById(
    id: UUID,
    username: String = USERNAME,
    roles: List<String> = listOf("ROLE_PRISONER_PAY__PRISONER_PAY_UI"),
    includeBearerAuth: Boolean = true,
  ) = webTestClient
    .get()
    .uri { uriBuilder ->
      uriBuilder
        .path("/pay-status-periods/$id")
        .build()
    }
    .accept(MediaType.APPLICATION_JSON)
    .headers(if (includeBearerAuth) setAuthorisation(roles = roles) else noAuthorisation())
    .exchange()

  private fun searchPayStatusPeriods(
    latestStartDate: LocalDate = today(),
    activeOnly: Boolean = true,
    prisonCode: String = PENTONVILLE,
    type: PayStatusType? = null,
    username: String = USERNAME,
    roles: List<String> = listOf("ROLE_PRISONER_PAY__PRISONER_PAY_UI"),
    includeBearerAuth: Boolean = true,
  ) = webTestClient
    .get()
    .uri { uriBuilder ->
      uriBuilder
        .path("/pay-status-periods")
        .queryParam("prisonCode", prisonCode)
        .queryParam("latestStartDate", latestStartDate)
        .queryParam("activeOnly", activeOnly)
        .apply { type?.let { queryParam("type", it) } }
        .build()
    }
    .accept(MediaType.APPLICATION_JSON)
    .headers(if (includeBearerAuth) setAuthorisation(roles = roles) else noAuthorisation())
    .exchange()
}
