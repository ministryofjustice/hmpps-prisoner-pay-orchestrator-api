package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.mapping

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper.prisoner

class PrisonerMappingTest {
  @Test
  fun `should map from api response to dto`() {
    val prisoner = prisoner()

    with(prisoner.toModel()) {
      assertThat(prisonerNumber).isEqualTo(prisoner.prisonerNumber)
      assertThat(firstName).isEqualTo(prisoner.firstName)
      assertThat(lastName).isEqualTo(prisoner.lastName)
      assertThat(cellLocation).isEqualTo(prisoner.cellLocation)
    }
  }
}
