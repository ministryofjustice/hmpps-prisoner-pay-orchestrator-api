package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Spy
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.today
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.integration.wiremock.PrisonerPayAPIMockServer
import java.time.LocalDateTime
import java.util.UUID

class PayStatusPeriodTest {
  @Spy
  private val server = PrisonerPayAPIMockServer().also { it.start() }
  private val client = PrisonerPayApiClient(WebClient.create("http://localhost:${server.port()}"))

  @AfterEach
  fun after() {
    server.stop()
  }

  @Test
  fun `should retrieve pay status periods`() {
    val latestStartDate = today()
    val payStatusPeriods = listOf(
      PayStatusPeriod(
        id = UUID.randomUUID(),
        prisonerNumber = "A1111AA",
        type = PayStatusType.LONG_TERM_SICK,
        startDate = today().minusDays(12),
        endDate = today().plusDays(1),
        createdBy = "BLOGGSJ",
        createdDateTime = LocalDateTime.now(),
      ),
      PayStatusPeriod(
        id = UUID.randomUUID(),
        prisonerNumber = "B2222BB",
        type = PayStatusType.LONG_TERM_SICK,
        startDate = today().minusDays(6),
        createdBy = "SMITHJ",
        createdDateTime = LocalDateTime.now().minusHours(2),
      ),
    )

    server.stubSearch(latestStartDate, false, payStatusPeriods)

    val result = client.search(latestStartDate, false)

    assertThat(result).isEqualTo(payStatusPeriods)
  }
}
