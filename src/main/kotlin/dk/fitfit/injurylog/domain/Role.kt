package dk.fitfit.injurylog.domain

import javax.persistence.*

@Entity
class Role(@Column(nullable = false, unique = true) val name: String,
           @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) val id: Long = 0) {
    companion object {
        const val ADMIN = "ROLE_ADMIN"
    }
}
