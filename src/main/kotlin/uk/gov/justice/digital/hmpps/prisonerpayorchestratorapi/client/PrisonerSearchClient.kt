package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.util.UriBuilder

@Component
class PrisonerSearchClient(private val prisonerSearchWebClient: WebClient) {
  private val responseFields = listOf(
    "prisonerNumber",
    "firstName",
    "lastName",
    "cellLocation",
  ).joinToString(",")

  suspend fun findByPrisonerNumber(prisonerNumber: String): Prisoner = prisonerSearchWebClient
    .get()
    .uri { uriBuilder: UriBuilder ->
      uriBuilder
        .path("/prisoner/{prisonerNumber}")
        .build(prisonerNumber)
    }
    .retrieve()
    .awaitBody()

  suspend fun findByPrisonerNumbers(prisonerNumbers: Set<String>): List<Prisoner> {
    if (prisonerNumbers.isEmpty()) return emptyList()

    require(prisonerNumbers.size <= 1000) { "Cannot handle more than 1000 prisoner numbers" }

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

  suspend fun search(prisonCode: String, searchTerm: String): PagedPrisoner = prisonerSearchWebClient
    .get()
    .uri { uriBuilder ->
      uriBuilder
        .path("/prison/$prisonCode/prisoners")
        .queryParam("term", searchTerm)
        .queryParam("size", 50)
        .queryParam("responseFields", responseFields)
        .build()
    }
    .retrieve()
    .bodyToMono(typeReference<PagedPrisoner>())
    .awaitSingle()
}

data class Prisoner(
  val prisonerNumber: String,
  val firstName: String,
  val lastName: String,
  val cellLocation: String? = null,
)

data class PrisonerNumbersSearch(val prisonerNumbers: Set<String>)

data class PagedPrisoner(
  val content: List<Prisoner>,
)
