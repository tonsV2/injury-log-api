package dk.fitfit.injurylog.service

import dk.fitfit.injurylog.domain.Tag
import java.time.LocalDateTime

interface TagService {
    fun findUpdates(updatedAfter: LocalDateTime): Set<Tag>
    fun findTagsStartingWith(name: String): Set<Tag>
    fun save(tag: Tag): Tag
    fun get(id: Long): Tag
}
