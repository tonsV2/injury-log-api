package dk.fitfit.injurylog.dto

import java.time.LocalDateTime

class InjuryResponse(val description: String,
                     val occurredAt: LocalDateTime,
                     val loggedAt: LocalDateTime,
                     val id: Long)
