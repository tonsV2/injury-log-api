package dk.fitfit.injurylog.dto

import java.net.URI
import java.time.LocalDateTime

class InjuryResponse(val description: String,
                     val occurredAt: LocalDateTime,
                     val loggedAt: LocalDateTime,
//                     val imageUris: List<URI>,
                     val id: Long)
