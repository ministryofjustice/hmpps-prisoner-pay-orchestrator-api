package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class PrisonerSearchClient(private val prisonerSearchWebClient: WebClient) {

  suspend fun searchByPrisonerNumbers(prisonerNumbers: Set<String>): List<Prisoner> {
    if (prisonerNumbers.isEmpty()) return emptyList()

    require(prisonerNumbers.size <= 1000) { "Cannot handle more than 1000 prisoner numbers" }

    val responseFields = listOf(
      "prisonerNumber",
      "firstName",
      "lastName",
      "cellLocation",
    ).joinToString(",")

    return prisonerSearchWebClient
      .post()
      .uri { uriBuilder ->
        uriBuilder
          .path("/prisoner-search/prisoner-numbers")
          .queryParam("responseFields", responseFields)
          .build()
      }
      .bodyValue(PrisonerNumbersSearch(prisonerNumbers))
      .retrieve()
      .awaitBody()
  }
}

data class Prisoner(
  val prisonerNumber: String,
  val firstName: String,
  val lastName: String,
  val cellLocation: String? = null,
)

data class PrisonerNumbersSearch(val prisonerNumbers: Set<String>)
