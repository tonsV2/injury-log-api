package dk.fitfit.injurylog.service

import dk.fitfit.injurylog.domain.Tag

interface TagService {
    fun findTagsStartingWith(name: String): Set<Tag>
    fun save(tag: Tag): Tag
}
