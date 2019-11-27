package dk.fitfit.injurylog.dto

import java.time.LocalDateTime

class InjuryResponse(val description: String,
                     val occurredAt: LocalDateTime,
                     val loggedAt: LocalDateTime,
                     val imageReferenceIds: List<Long>,
                     val id: Long)
