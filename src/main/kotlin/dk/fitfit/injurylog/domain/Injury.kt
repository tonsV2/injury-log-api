package dk.fitfit.injurylog.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Injury(
        val description: String,
        @JsonIgnore @ManyToOne val user: User,
        val occurredAt: LocalDateTime = LocalDateTime.now(),
        @ManyToMany
        @Fetch(value = FetchMode.SUBSELECT)
        @JoinTable(name = "injury_tags",
                joinColumns = [(JoinColumn(name = "injury_id", referencedColumnName = "id"))])
        val tags: MutableList<Tag> = mutableListOf(),
        val loggedAt: LocalDateTime = LocalDateTime.now(),
        @OneToMany
        @Fetch(FetchMode.SUBSELECT)
        val imageReferences: MutableList<ImageReference> = mutableListOf(),
        @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) val id: Long = 0
)
