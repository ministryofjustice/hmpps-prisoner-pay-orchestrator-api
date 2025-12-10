package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Spy
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.integration.wiremock.PrisonerSearchMockServer
import java.util.*

class PrisonerSearchClientTest {
  @Spy
  private val server = PrisonerSearchMockServer().also { it.start() }
  private val client = PrisonerSearchClient(WebClient.create("http://localhost:${server.port()}"))

  @AfterEach
  fun after() {
    server.stop()
  }

  @Test
  fun `findByPrisonerNumber - returns prisoners`() = runTest {
    val prisonerNumbers = setOf("A1111AA", "B2222BB")

    val prisoners = listOf(
      Prisoner(
        prisonerNumber = "A1111AA",
        firstName = "JOHN",
        lastName = "SMITH",
        cellLocation = "A-1-001",
      ),
      Prisoner(
        prisonerNumber = "B2222BB",
        firstName = "JONES",
        lastName = "ALAN",
        cellLocation = "B-2-002",
      ),
    )

    server.stubSearchByPrisonerNumbers(prisonerNumbers, prisoners)

    val result = client.searchByPrisonerNumbers(prisonerNumbers)

    assertThat(result).isEqualTo(prisoners)
  }

  @Test
  fun `findByPrisonerNumber - throws and exception if more than 1000 prisoners`() {
    val prisonerNumbers = generateSequence { UUID.randomUUID().toString() }
      .distinct()
      .take(1001)
      .toSet()

    assertThatThrownBy {
      runTest {
        client.searchByPrisonerNumbers(prisonerNumbers)
      }
    }
      .isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("Cannot handle more than 1000 prisoner numbers")
  }

  @Test
  fun `findByPrisonerNumber - returns an empty list if supplied prisoner numbers is empty`() = runTest {
    assertThat(client.searchByPrisonerNumbers(emptySet())).isEmpty()
  }
}
