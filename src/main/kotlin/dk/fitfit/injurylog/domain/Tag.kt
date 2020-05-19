package dk.fitfit.injurylog.domain

import dk.fitfit.injurylog.domain.core.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(indexes = [Index(columnList = "name")])
class Tag(
        @Column(unique = true) val name: String,
//          @ManyToMany(mappedBy = "tags")
//          val injuries: MutableList<Injury> = mutableListOf(),
        id: Long = 0
) : BaseEntity(id)
