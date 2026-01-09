package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PrisonerPayApiClient
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PrisonerSearchClient
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.dto.PayStatusPeriod
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.mapping.toModel
import java.time.LocalDate
import java.util.UUID

@Service
class PrisonerPayService(
  private val prisonerPayApiClient: PrisonerPayApiClient,
  private val prisonerSearchClient: PrisonerSearchClient,
) {
  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  suspend fun getById(id: UUID) = prisonerPayApiClient.getById(id).let { payStatusPeriod ->
    prisonerSearchClient.findByPrisonerNumber(payStatusPeriod.prisonerNumber).let { prisoner ->
      payStatusPeriod.toModel(prisoner)
    }
  }

  suspend fun search(prisonCode: String, latestStartDate: LocalDate, activeOnly: Boolean): List<PayStatusPeriod> {
    val payStatusPeriods = prisonerPayApiClient.search(
      prisonCode = prisonCode,
      latestStartDate = latestStartDate,
      activeOnly = activeOnly,
    )

    val prisonerNumbers = payStatusPeriods.map { it.prisonerNumber }.toSet()

    // TODO: What happens if prisoner is not in the expected prison?
    val prisoners = prisonerSearchClient.findByPrisonerNumbers(prisonerNumbers).associateBy { it.prisonerNumber }

    (prisonerNumbers - prisoners.keys)
      .takeIf { it.isNotEmpty() }
      ?.let { missingPrisonerNumbers ->
        log.warn("Prisoners ${missingPrisonerNumbers.joinToString(",")} not found in prisoner search response")
      }

    /**
     * TODO: For now just filtering out prisoners not returned by prisoner search
     * Need to consider how to handle this in future including not in prison, etc
     */
    return payStatusPeriods
      .filter { prisoners.containsKey(it.prisonerNumber) }
      .map { it.toModel(prisoners[it.prisonerNumber]!!) }
  }
}
