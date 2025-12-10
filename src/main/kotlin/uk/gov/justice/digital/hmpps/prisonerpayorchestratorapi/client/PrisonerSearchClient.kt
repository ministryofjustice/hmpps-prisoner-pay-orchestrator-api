package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class PrisonerSearchClient(private val prisonerSearchWebClient: WebClient) {
  suspend fun searchByPrisonerNumbers(prisonerNumbers: Set<String>): List<Prisoner> {
    if (prisonerNumbers.isEmpty()) return emptyList()

    require(prisonerNumbers.size <= 1000) { "Cannot handle more than 1000 prisoner numbers" }

    return prisonerSearchWebClient
      .post()
      .uri("/prisoner-search/prisoner-numbers")
      .bodyValue(PrisonerNumbersSearch(prisonerNumbers))
      .retrieve()
      .awaitBody()
  }
}

data class Prisoner(
  val prisonerNumber: String,
  val firstName: String,
  val lastName: String,
  val cellLocation: String,
)

data class PrisonerNumbersSearch(val prisonerNumbers: Set<String>)
