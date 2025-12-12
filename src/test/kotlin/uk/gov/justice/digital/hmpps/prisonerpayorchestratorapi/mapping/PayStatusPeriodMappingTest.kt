package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.mapping

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.payStatusPeriod
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.prisoner

class PayStatusPeriodMappingTest {

  @Test
  fun `should map from api response to dto`() {
    val prisonerPayStatus = payStatusPeriod()
    val prisoner = prisoner()

    with(prisonerPayStatus.toModel(prisoner)) {
      assertThat(id).isEqualTo(prisonerPayStatus.id)
      assertThat(prisonerNumber).isEqualTo(prisonerPayStatus.prisonerNumber)
      assertThat(firstName).isEqualTo(prisoner.firstName)
      assertThat(lastName).isEqualTo(prisoner.lastName)
      assertThat(cellLocation).isEqualTo(prisoner.cellLocation)
      assertThat(type).isEqualTo(prisonerPayStatus.type)
      assertThat(startDate).isEqualTo(prisonerPayStatus.startDate)
      assertThat(endDate).isEqualTo(prisonerPayStatus.endDate)
      assertThat(createdBy).isEqualTo(prisonerPayStatus.createdBy)
      assertThat(createdDateTime).isEqualTo(prisonerPayStatus.createdDateTime)
    }
  }

  @Test
  fun `should from map api response to dto when cellLocation is null`() {
    val prisonerPayStatus = payStatusPeriod()
    val prisoner = prisoner(cellLocation = null)

    with(prisonerPayStatus.toModel(prisoner)) {
      assertThat(id).isEqualTo(prisonerPayStatus.id)
      assertThat(prisonerNumber).isEqualTo(prisonerPayStatus.prisonerNumber)
      assertThat(firstName).isEqualTo(prisoner.firstName)
      assertThat(lastName).isEqualTo(prisoner.lastName)
      assertThat(cellLocation).isNull()
      assertThat(type).isEqualTo(prisonerPayStatus.type)
      assertThat(startDate).isEqualTo(prisonerPayStatus.startDate)
      assertThat(endDate).isEqualTo(prisonerPayStatus.endDate)
      assertThat(createdBy).isEqualTo(prisonerPayStatus.createdBy)
      assertThat(createdDateTime).isEqualTo(prisonerPayStatus.createdDateTime)
    }
  }

  @Test
  fun `should map from api response to dto when end date is null`() {
    val prisonerPayStatus = payStatusPeriod(endDate = null)
    val prisoner = prisoner(cellLocation = null)

    with(prisonerPayStatus.toModel(prisoner)) {
      assertThat(id).isEqualTo(prisonerPayStatus.id)
      assertThat(prisonerNumber).isEqualTo(prisonerPayStatus.prisonerNumber)
      assertThat(firstName).isEqualTo(prisoner.firstName)
      assertThat(lastName).isEqualTo(prisoner.lastName)
      assertThat(cellLocation).isNull()
      assertThat(type).isEqualTo(prisonerPayStatus.type)
      assertThat(startDate).isEqualTo(prisonerPayStatus.startDate)
      assertThat(endDate).isNull()
      assertThat(createdBy).isEqualTo(prisonerPayStatus.createdBy)
      assertThat(createdDateTime).isEqualTo(prisonerPayStatus.createdDateTime)
    }
  }
}
