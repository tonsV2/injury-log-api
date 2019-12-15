package dk.fitfit.injurylog.repository

import dk.fitfit.injurylog.domain.Tag
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@MicronautTest
internal open class TagRepositoryTest(private val tagRepository: TagRepository) {
    @Test
    fun `Find tags starting with`() {
        // Given
        val query = "tag"

        val name = "tagName"
        val tag = tagRepository.save(Tag(name))
        val name2 = "tagName2"
        val tag2 = tagRepository.save(Tag(name2))
        val name3 = "otherTagName"
        tagRepository.save(Tag(name3))

        // When
        val tags = tagRepository.findTagsStartingWith(query)

        // Then
        assertEquals(2, tags.size)
        assertTrue(tags.contains(tag))
        assertTrue(tags.contains(tag2))
    }
}
