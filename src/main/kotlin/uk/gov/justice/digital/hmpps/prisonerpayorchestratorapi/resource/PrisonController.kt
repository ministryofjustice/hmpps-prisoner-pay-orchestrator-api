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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.dto.Prisoner
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.service.CandidateService
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@RestController
@RequestMapping(value = ["prison"], produces = [MediaType.APPLICATION_JSON_VALUE])
@Tag(
  name = "prison",
)
class PrisonController(
  private val candidateService: CandidateService,
) {
  @GetMapping("/{prisonCode}/candidate-search")
  @PreAuthorize("hasRole('ROLE_PRISONER_PAY__PRISONER_PAY_UI')")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Retrieve a list prisoners based in search criteria",
    description = """
      Will return prisoners in the prison where the first or last names start with the search term or where the prisoner number is an exact match.
      
      Will return up to 50 results.
    """,
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Returns th list of matched prisoners",
        content = [Content(mediaType = "application/json", array = ArraySchema(schema = Schema(implementation = Prisoner::class)))],
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
  suspend fun search(
    @PathVariable
    @Parameter(description = "The prison code to search within", example = "PVI")
    prisonCode: String,
    @RequestParam("searchTerm", required = true)
    @Parameter(description = "The search term which", required = true, example = "Blo")
    searchTerm: String,
  ) = candidateService.search(prisonCode, searchTerm)
}
