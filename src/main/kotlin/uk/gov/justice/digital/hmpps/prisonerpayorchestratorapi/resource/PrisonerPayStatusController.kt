package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.dto.PayStatusPeriod
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.service.PrisonerPayService
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse
import java.time.LocalDate

@RestController
@RequestMapping(value = ["pay-status-periods"], produces = [MediaType.APPLICATION_JSON_VALUE])
@Tag(
  name = "Pay Status Periods",
)
class PrisonerPayStatusController(private val prisonerPayService: PrisonerPayService) {

  @GetMapping
  @PreAuthorize("permitAll()") // TODO: Add roles
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Retrieve a list of pay status periods ordered by start date",
    // description = "Requires role <TODO>",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Returns the new prisoner status period",
        content = [Content(mediaType = "application/json", array = ArraySchema(schema = Schema(implementation = PayStatusPeriod::class)))],
      ),
      ApiResponse(
        responseCode = "400",
        description = "Invalid Request",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Missing required role. Requires the <TODO> role with write scope.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  fun search(
    @RequestParam(value = "latestStartDate")
    @Parameter(description = "The latest start date the pay status periods started on", example = "2025-07-18")
    latestStartDate: LocalDate,
    @RequestParam(value = "activeOnly", required = false, defaultValue = "true")
    @Parameter(description = "Whether to return results which are currently active, i.e. the end date is null or not before today", example = "true")
    activeOnly: Boolean = true,
  ) = prisonerPayService.search(latestStartDate, activeOnly)
}
