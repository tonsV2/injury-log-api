package dk.fitfit.injurylog.dto

import java.time.LocalDateTime

data class InjuryRequest(val description: String,
                         val occurredAt: LocalDateTime = LocalDateTime.now())
