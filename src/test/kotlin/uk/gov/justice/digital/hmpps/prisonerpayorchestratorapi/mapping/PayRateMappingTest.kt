package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.mapping

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.UUID1
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.payRate

class PayRateMappingTest {

  @Test
  fun `should map from api response to dto`() {
    val payRate = payRate()
    val payRateDto = payRate.toModel(prisonerCount = 5)
    with(payRateDto) {
      assertThat(id).isEqualTo(UUID1)
      assertThat(prisonCode).isEqualTo(payRate.prisonCode)
      assertThat(type).isEqualTo(payRate.type)
      assertThat(startDate).isEqualTo(payRate.startDate)
      assertThat(rate).isEqualTo(payRate.rate)
      assertThat(createdBy).isEqualTo(payRate.createdBy)
      assertThat(createdDateTime).isEqualTo(payRate.createdDateTime)
      assertThat(prisonerCount).isEqualTo(5)
      assertThat(updatedDateTime).isEqualTo(payRate.updatedDateTime)
      assertThat(updatedBy).isEqualTo(payRate.updatedBy)
    }
  }
}
