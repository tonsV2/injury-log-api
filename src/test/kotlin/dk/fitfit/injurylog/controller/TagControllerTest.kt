package dk.fitfit.injurylog.controller

import dk.fitfit.injurylog.configuration.AuthenticationConfiguration
import dk.fitfit.injurylog.controller.client.TagClient
import dk.fitfit.injurylog.dto.TagRequest
import dk.fitfit.injurylog.dto.TagResponse
import dk.fitfit.injurylog.repository.TagRepository
import io.micronaut.test.annotation.MicronautTest
import io.mockk.MockKAnnotations
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

@MicronautTest
internal class TagControllerTest(
        private val authenticationConfiguration: AuthenticationConfiguration,
        private val tagClient: TagClient,
        private val tagRepository: TagRepository
) : SecuredControllerTest() {

    private lateinit var authorization: String

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        tagRepository.deleteAll()

        authorization = getAuthorization(authenticationConfiguration.testUserEmail, authenticationConfiguration.testUserPassword)
    }

    @Test
    fun `Find updated tags - Return 1 tag`() {
        // Given
        val name1 = "tagName1"
        val updated1 = createTag(name1).updated

        val name2 = "tagName2"
        createTag(name2)

        // When
        val tags = tagClient.findUpdates(updated1.toEpochMilli(), authorization)

        // Then
        assertEquals(1, tags.size)
        assertEquals(name2, tags.first().name)
    }

    @Test
    fun `Find updated tags - Return 0 tag`() {
        // Given
        val name1 = "tagName1"
        createTag(name1)

        val name2 = "tagName2"
        val updated2 = createTag(name2).updated

        // When
        val tags = tagClient.findUpdates(updated2.toEpochMilli(), authorization)

        // Then
        assertEquals(0, tags.size)
    }

    @Test
    fun `Save a tag`() {
        // Given
        val name = "name"
        val request = TagRequest(name)

        // When
        val response = tagClient.postTag(request, authorization)

        // Then
        val tagResponse = response.body.get()

        assertNotNull(tagResponse.id)
        assertNotEquals(0L, tagResponse.id)
        assertEquals(name, tagResponse.name)
    }

    @Test
    fun `Find tags starting with`() {
        val query = "tag"
        // Given
        val name1 = "tagName1"
        val id1 = createTag(name1).id

        val name2 = "tagName2"
        val id2 = createTag(name2).id

        val name3 = "otherTagName3"
        val id3 = createTag(name3).id

        // When
        val tags = tagClient.getTagsStartingWith(query, authorization)

        // Then
        val tag1 = tags.body.get().first { it.id == id1 }
        val tag2 = tags.body.get().first { it.id == id2 }
        val tag3 = tags.body.get().filter { it.id == id3 }

        assertEquals(2, tags.body.get().size)

        assertEquals(id1, tag1.id)
        assertEquals(name1, tag1.name)

        assertEquals(id2, tag2.id)
        assertEquals(name2, tag2.name)

        assertTrue(tag3.isEmpty())
    }

    private fun createTag(name: String): TagResponse {
        val request = TagRequest(name)
        return tagClient.postTag(request, authorization).body.get()
    }

    private fun LocalDateTime.toEpochMilli() = this.toInstant(ZoneOffset.UTC).toEpochMilli()
}
