package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PrisonerPayOrchestratorApi

fun main(args: Array<String>) {
  runApplication<PrisonerPayOrchestratorApi>(*args)
}
