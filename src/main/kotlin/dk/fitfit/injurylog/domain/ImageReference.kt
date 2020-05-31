package dk.fitfit.injurylog.domain

import dk.fitfit.injurylog.domain.core.BaseEntity
import javax.persistence.Entity

@Entity
class ImageReference(
        val key: String,
        id: Long = 0
) : BaseEntity(id)
