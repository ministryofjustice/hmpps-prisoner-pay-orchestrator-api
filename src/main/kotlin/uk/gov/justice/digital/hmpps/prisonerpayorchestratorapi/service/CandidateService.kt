package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client.PrisonerSearchClient
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.mapping.toModel

@Service
class CandidateService(
  private val prisonerSearchClient: PrisonerSearchClient,
) {
  suspend fun search(prisonCode: String, searchTerm: String) = prisonerSearchClient.search(prisonCode, searchTerm).content.map { it.toModel() }
}
