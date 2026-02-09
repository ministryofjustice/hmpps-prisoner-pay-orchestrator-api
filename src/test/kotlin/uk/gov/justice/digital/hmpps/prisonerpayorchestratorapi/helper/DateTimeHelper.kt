package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.helper

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun yesterday(): LocalDate = today().minusDays(1)
fun today(): LocalDate = LocalDate.now()
fun tomorrow(): LocalDate = today().plusDays(1)
val clock: Clock = Clock.fixed(Instant.parse("2026-02-01T00:00:00Z"), ZoneId.of("Europe/London"))
