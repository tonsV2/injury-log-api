package dk.fitfit.injurylog.domain

import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
data class User(@Column(nullable = false, unique = true) val email: String,
                val created: LocalDateTime = LocalDateTime.now(),
                @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0)
