package dk.fitfit.injurylog.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import dk.fitfit.injurylog.domain.core.BaseEntity
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.time.LocalDateTime
import javax.persistence.*
import javax.persistence.FetchType.EAGER

@Entity
class Injury(
        val description: String,
        @JsonIgnore @ManyToOne val user: User,
        val occurredAt: LocalDateTime = LocalDateTime.now(),
        @ManyToMany(fetch = EAGER)
        @Fetch(value = FetchMode.SUBSELECT)
        @JoinTable(name = "injury_tags",
                joinColumns = [(JoinColumn(name = "injury_id", referencedColumnName = "id"))])
        val tags: MutableList<Tag> = mutableListOf(),
        val loggedAt: LocalDateTime = LocalDateTime.now(),
        @OneToMany(fetch = EAGER)
        @Fetch(FetchMode.SUBSELECT)
        val imageReferences: MutableList<ImageReference> = mutableListOf(),
        id: Long = 0
) : BaseEntity(id)
