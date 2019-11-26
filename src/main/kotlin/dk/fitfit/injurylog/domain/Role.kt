package dk.fitfit.injurylog.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class Role(@Column(nullable = false, unique = true) val name: String,
           @JsonIgnore @ManyToMany val users: List<User> = listOf(),
           @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0)
