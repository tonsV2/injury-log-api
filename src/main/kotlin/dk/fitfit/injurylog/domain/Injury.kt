package dk.fitfit.injurylog.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class Injury(val description: String,
                  @JsonIgnore @ManyToOne val user: User?,
                  val occurrence: LocalDateTime = LocalDateTime.now(),
                  val logged: LocalDateTime = LocalDateTime.now(),
                  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0)
