package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Component
class PrisonerPayApiClient(private val prisonerPayWebClient: WebClient) {
  fun search(latestStartDate: LocalDate, activeOnly: Boolean): List<PayStatusPeriod> = prisonerPayWebClient
    .get()
    .uri("/pay-status-periods?latestStartDate=$latestStartDate&activeOnly=$activeOnly")
    .retrieve()
    .bodyToMono(typeReference<List<PayStatusPeriod>>())
    .block()!!
}

data class PayStatusPeriod(
  val id: UUID,
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
