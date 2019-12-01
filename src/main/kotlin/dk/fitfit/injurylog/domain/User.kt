package dk.fitfit.injurylog.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "users") // Postgres doesn't like the table name "user"
class User(@Column(nullable = false, unique = true) val email: String,
           val created: LocalDateTime = LocalDateTime.now(),
           @JsonIgnore @OneToMany val injuries: List<Injury> = listOf(),
           @ManyToMany(fetch = FetchType.EAGER)
           @JoinTable(name = "users_roles",
                   joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
                   inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")])
           val roles: MutableList<Role> = mutableListOf(),
           @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) val id: Long = 0)
