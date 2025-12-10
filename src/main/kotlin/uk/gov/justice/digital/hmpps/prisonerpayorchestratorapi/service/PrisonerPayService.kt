package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.service

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PrisonerPayApiClient
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PrisonerSearchClient
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.mapping.toModel
import java.time.LocalDate

@Service
class PrisonerPayService(
  private val prisonerPayApiClient: PrisonerPayApiClient,
  private val prisonerSearchClient: PrisonerSearchClient,
) {
  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  fun search(latestStartDate: LocalDate, activeOnly: Boolean) = runBlocking {
    val payStatusPeriods = prisonerPayApiClient.search(latestStartDate, activeOnly)

    val prisonerNumbers = payStatusPeriods.map { it.prisonerNumber }.toSet()

    val prisoners = async { prisonerSearchClient.searchByPrisonerNumbers(prisonerNumbers) }.await().associateBy { it.prisonerNumber }

    (prisonerNumbers - prisoners.keys)
      .takeIf { it.isNotEmpty() }
      ?.let { missingPrisonerNumbers ->
        log.warn("Prisoners ${missingPrisonerNumbers.joinToString(",")} not found in prisoner search response")
      }

    /**
     * TODO: For now just filtering out prisoners not returned by prisoner search
     * Need to consider how to handle this in future including not in prison, etc
     */
    payStatusPeriods
      .filter { prisoners.containsKey(it.prisonerNumber) }
      .map { it.toModel(prisoners[it.prisonerNumber]!!) }
  }
}
