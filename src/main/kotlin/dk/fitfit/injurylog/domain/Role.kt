package dk.fitfit.injurylog.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class Role(@Column(nullable = false, unique = true) val name: String,
           @JsonIgnore @ManyToMany(mappedBy = "roles") val users: List<User> = listOf(),
           @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) val id: Long = 0) {
    companion object {
        const val ADMIN = "ROLE_ADMIN"
    }
}
