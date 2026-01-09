package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Spy
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.PENTONVILLE
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.UUID1
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.payStatusPeriod
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.today
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.integration.wiremock.PrisonerPayAPIMockServer
import java.time.LocalDateTime

class PrisonerPayApiClientTest {
  @Spy
  private val server = PrisonerPayAPIMockServer().also { it.start() }
  private val client = PrisonerPayApiClient(WebClient.create("http://localhost:${server.port()}"))

  @AfterEach
  fun after() {
    server.stop()
  }

  @Test
  fun `should retrieve a pay status period`() = runTest {
    val latestStartDate = today()

    val payStatusPeriod = payStatusPeriod(
      prisonerNumber = "A1111AA",
      startDate = today().minusDays(12),
      endDate = today().plusDays(1),
      createdBy = "BLOGGSJ",
      createdDateTime = LocalDateTime.now(),
    )

    server.stubGetById(UUID1, payStatusPeriod)

    val result = client.getById(UUID1)

    assertThat(result).isEqualTo(payStatusPeriod)
  }

  @Test
  fun `should retrieve pay status periods`() = runTest {
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
    )

    server.stubSearch(
      latestStartDate = latestStartDate,
      activeOnly = false,
      response = payStatusPeriods,
    )

    val result = client.search(PENTONVILLE, latestStartDate, false)

    assertThat(result).isEqualTo(payStatusPeriods)
  }
}
