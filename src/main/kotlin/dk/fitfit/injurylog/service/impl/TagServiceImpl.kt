package dk.fitfit.injurylog.service.impl

import dk.fitfit.injurylog.domain.Tag
import dk.fitfit.injurylog.repository.TagRepository
import dk.fitfit.injurylog.service.TagService
import javax.inject.Singleton

@Singleton
class TagServiceImpl(private val tagRepository: TagRepository) : TagService {
    override fun findTagsStartingWith(name: String): Set<Tag> = tagRepository.findTagsStartingWith(name)

    override fun save(tag: Tag): Tag = tagRepository.save(tag)
}
