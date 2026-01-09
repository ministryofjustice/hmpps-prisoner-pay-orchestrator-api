package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.client

import org.springframework.core.ParameterizedTypeReference

inline fun <reified T : Any> typeReference() = object : ParameterizedTypeReference<T>() {}
