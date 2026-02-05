package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
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
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.dto.PayRateDto
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.service.PayRateService
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@RestController
@RequestMapping(value = ["pay-rates"], produces = [MediaType.APPLICATION_JSON_VALUE])
@Tag(
  name = "Pay Rates",
)
@AuthApiResponses
class PayRateController(private val payRateService: PayRateService) {
  @GetMapping("/prison/{prisonCode}")
  @PreAuthorize("hasRole('ROLE_PRISONER_PAY__PRISONER_PAY_UI')")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Retrieve all current and future long term sick pay rates",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Returns the list of current and future long term sick pay rates",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = PayRateDto::class))],
      ),
      ApiResponse(
        responseCode = "400",
        description = "Invalid Request",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  suspend fun getCurrentAndFuturePayRates(
    @PathVariable
    @Parameter(description = "The prison code")
    prisonCode: String,
  ): List<PayRateDto> = payRateService.getCurrentAndFuturePayRates(prisonCode)
}
