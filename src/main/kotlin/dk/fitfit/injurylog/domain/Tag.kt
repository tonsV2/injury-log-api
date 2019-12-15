package dk.fitfit.injurylog.domain

import javax.persistence.*

@Entity
@Table(indexes = [Index(columnList = "name")])
class Tag(@Column(unique = true) val name: String,
//          @ManyToMany(mappedBy = "tags")
//          val injuries: MutableList<Injury> = mutableListOf(),
          @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) val id: Long = 0)
