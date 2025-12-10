package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.hmpps.kotlin.auth.authorisedWebClient
import uk.gov.justice.hmpps.kotlin.auth.healthWebClient
import java.time.Duration

@Configuration
class WebClientConfiguration(
  @param:Value($$"${api.base.url.hmpps-auth}") val hmppsAuthBaseUri: String,
  @param:Value($$"${api.base.url.prisoner-pay-api}") val prisonerPayApiBaseUri: String,
  @param:Value($$"${api.base.url.prisoner-search}") val prisonerSearchBaseUri: String,
  @param:Value($$"${api.health-timeout:2s}") val healthTimeout: Duration,
  @param:Value($$"${api.timeout:10s}") val timeout: Duration,
) {
  // HMPPS Auth health ping is required if your service calls HMPPS Auth to get a token to call other services
  @Bean
  fun hmppsAuthHealthWebClient(builder: WebClient.Builder): WebClient = builder.healthWebClient(hmppsAuthBaseUri, healthTimeout)

  @Bean
  fun prisonerPayHealthWebClient(builder: WebClient.Builder): WebClient = builder.healthWebClient(prisonerPayApiBaseUri, healthTimeout)

  @Bean
  fun prisonerPayWebClient(authorizedClientManager: OAuth2AuthorizedClientManager, builder: WebClient.Builder): WebClient = builder.authorisedWebClient(authorizedClientManager, "prisoner-pay-api", prisonerPayApiBaseUri, timeout)

  @Bean
  fun prisonerSearchHealthWebClient(builder: WebClient.Builder) = builder.healthWebClient(prisonerSearchBaseUri, healthTimeout)

  @Bean
  fun prisonerSearchWebClient(authorizedClientManager: OAuth2AuthorizedClientManager, builder: WebClient.Builder) = builder.authorisedWebClient(authorizedClientManager, "prisoner-search", prisonerSearchBaseUri, timeout)
}
