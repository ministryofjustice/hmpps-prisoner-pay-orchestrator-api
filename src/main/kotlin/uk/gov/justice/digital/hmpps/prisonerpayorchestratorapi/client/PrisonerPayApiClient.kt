package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Component
class PrisonerPayApiClient(private val prisonerPayWebClient: WebClient) {
  suspend fun getById(id: UUID): PayStatusPeriod = prisonerPayWebClient.get().uri("/pay-status-periods/$id").retrieve().awaitBody()

  suspend fun search(prisonCode: String, latestStartDate: LocalDate, activeOnly: Boolean): List<PayStatusPeriod> = prisonerPayWebClient
    .get()
    .uri { uriBuilder ->
      uriBuilder
        .path("/pay-status-periods")
        .queryParam("prisonCode", prisonCode)
        .queryParam("latestStartDate", latestStartDate)
        .queryParam("activeOnly", activeOnly)
        .build()
    }
    .retrieve()
    .awaitBody()
}

data class PayStatusPeriod(
  val id: UUID,
  val prisonCode: String,
  val prisonerNumber: String,
  val type: PayStatusType,
  val startDate: LocalDate,
  val endDate: LocalDate? = null,
  val createdBy: String,
  val createdDateTime: LocalDateTime,
)

enum class PayStatusType {
  LONG_TERM_SICK,
}
