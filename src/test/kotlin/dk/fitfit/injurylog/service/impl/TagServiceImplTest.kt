package dk.fitfit.injurylog.service.impl

import dk.fitfit.injurylog.domain.Tag
import dk.fitfit.injurylog.repository.TagRepository
import io.micronaut.test.annotation.MicronautTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@MicronautTest
internal class TagServiceImplTest {
    private val tagRepository = mockk<TagRepository>()
    private val tagService = TagServiceImpl(tagRepository)

    @Test
    fun `Find tags starting with`() {
        // Given
        val query = "tag"
        val tag = Tag("tagName", 1)
        val tag2 = Tag("tagName2", 2)
        every { tagRepository.findTagsStartingWith(query) } returns setOf(tag, tag2)

        // When
        val tags = tagService.findTagsStartingWith(query)

        // Then
        assertEquals(2, tags.size)
        assertTrue(tags.contains(tag))
        assertTrue(tags.contains(tag2))
        verify(exactly = 1) { tagRepository.findTagsStartingWith(query) }
    }

    @Test
    fun `Save a tag`() {
        // Given
        val id = 123L
        val name = "tagName"
        val tag = Tag(name, id)
        every { tagRepository.save(tag) } returns tag

        // When
        val saved = tagService.save(tag)

        // Then
        assertEquals(tag.id, saved.id)
        assertEquals(tag.name, saved.name)
        verify(exactly = 1) { tagRepository.save(tag) }
    }
}
