package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Prisoner")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Prisoner(
  @Schema(description = "The prisoner number (NOMIS ID)", example = "A1234AA")
  val prisonerNumber: String,

  @Schema(description = "The prisoner's first name", example = "Joe")
  val firstName: String,

  @Schema(description = "The prisoner's last name", example = "Bloggs")
  val lastName: String,

  @Schema(description = "The prisoner's residential cell location when inside the prison", example = "A-1-002")
  val cellLocation: String? = null,
)
