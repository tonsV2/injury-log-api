package dk.fitfit.injurylog.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class User(@Column(nullable = false, unique = true) val email: String,
           val created: LocalDateTime = LocalDateTime.now(),
           @JsonIgnore @OneToMany val injuries: List<Injury> = listOf(),
           @ManyToMany(fetch = FetchType.EAGER) val roles: List<Role> = listOf(),
           @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0)
