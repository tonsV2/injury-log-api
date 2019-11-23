package dk.fitfit.injurylog.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
data class User(@Column(nullable = false, unique = true) val email: String,
                val created: LocalDateTime = LocalDateTime.now(),
                @JsonIgnore @OneToMany(fetch = FetchType.LAZY) val injuries: MutableList<Injury> = ArrayList(),
                @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0)
