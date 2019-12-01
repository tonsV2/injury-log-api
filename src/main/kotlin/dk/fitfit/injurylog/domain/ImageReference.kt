package dk.fitfit.injurylog.domain

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class ImageReference(val key: String,
                     @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) val id: Long = 0)
