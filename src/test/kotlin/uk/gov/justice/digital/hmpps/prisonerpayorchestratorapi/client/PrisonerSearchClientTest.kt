package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Spy
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.PENTONVILLE
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.prisoner
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

  @Nested
  @DisplayName("Find prisoner by prisoner number")
  inner class FindByPrisonerNumber {
    @Test
    fun `returns the prisoner`() = runTest {
      val prisoner = prisoner(
        prisonerNumber = "A1111AA",
        firstName = "JOHN",
        lastName = "SMITH",
        cellLocation = "A-1-001",
      )

      server.stubGetPrisonerById("A1111AA", prisoner)

      val result = client.findByPrisonerNumber("A1111AA")

      assertThat(result).isEqualTo(prisoner)
    }
  }

  @Nested
  @DisplayName("Find prisoners by prisoner numbers")
  inner class FindByPrisonerNumbers {
    @Test
    fun `returns prisoners`() = runTest {
      val prisonerNumbers = setOf("A1111AA", "B2222BB")

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

      server.stubSearchByPrisonerNumbers(prisonerNumbers, prisoners)

      val result = client.findByPrisonerNumbers(prisonerNumbers)

      assertThat(result).isEqualTo(prisoners)
    }

    @Test
    fun `throws and exception if more than 1000 prisoners`() {
      val prisonerNumbers = generateSequence { UUID.randomUUID().toString() }
        .distinct()
        .take(1001)
        .toSet()

      assertThatThrownBy {
        runTest {
          client.findByPrisonerNumbers(prisonerNumbers)
        }
      }
        .isInstanceOf(IllegalArgumentException::class.java)
        .hasMessage("Cannot handle more than 1000 prisoner numbers")
    }

    @Test
    fun `returns an empty list if supplied prisoner numbers is empty`() = runTest {
      assertThat(client.findByPrisonerNumbers(emptySet())).isEmpty()
    }
  }

  @Nested
  @DisplayName("Find prisoners by search term")
  inner class FindPrisonersBySearchTerm {
    @Test
    fun `returns prisoners`() = runTest {
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
          lastName = "JOSEPH",
          cellLocation = "B-2-002",
        ),
      )

      server.stubSearchBySearchTerm(searchTerm = "Jo", prisoners = prisoners)

      val result = client.search(PENTONVILLE, "Jo")

      assertThat(result).isEqualTo(PagedPrisoner(prisoners))
    }
  }
}
