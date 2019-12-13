package dk.fitfit.injurylog.controller

import dk.fitfit.injurylog.configuration.AuthenticationConfiguration
import dk.fitfit.injurylog.controller.client.InjuryClient
import dk.fitfit.injurylog.domain.ImageReference
import dk.fitfit.injurylog.dto.InjuryRequest
import dk.fitfit.injurylog.dto.InjuryResponse
import io.micronaut.http.MediaType
import io.micronaut.http.client.multipart.MultipartBody
import io.micronaut.test.annotation.MicronautTest
import io.mockk.MockKAnnotations
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDateTime
import kotlin.test.assertTrue

@MicronautTest
internal class InjuryControllerTest(private val authenticationConfiguration: AuthenticationConfiguration, private val injuryClient: InjuryClient) : SecuredControllerTest() {
    private lateinit var authorization: String

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        authorization = getAuthorization(authenticationConfiguration.testUserEmail, authenticationConfiguration.testUserPassword)
    }

    @Test
    fun `Post an injury`() {
        // Given
        val description = "description"
        val occurredAt = LocalDateTime.now()
        val request = InjuryRequest(description, occurredAt)

        // When
        val response = injuryClient.postInjury(request, authorization)

        // Then
        assertEquals(description, response.description)
        assertEquals(occurredAt, response.occurredAt)
    }

    @Test
    fun `Get an injury`() {
        // Given
        val description = "description"
        val occurredAt = LocalDateTime.now()
        val id = createInjury(description, occurredAt).id

        // When
        val response = injuryClient.getInjury(id, authorization)

        // Then
        assertEquals(id, response.id)
        assertEquals(description, response.description)
        assertEquals(occurredAt, response.occurredAt)
    }

    @Test
    fun `Get all injuries`() {
        // Given
        val description1 = "description1"
        val occurredAt1 = LocalDateTime.now()
        val id1 = createInjury(description1, occurredAt1).id

        val description2 = "description2"
        val occurredAt2 = LocalDateTime.now()
        val id2 = createInjury(description2, occurredAt2).id

        // When
        val response = injuryClient.getInjuries(authorization)

        // Then
        val injury1 = response.filter { it.id == id1 }.first()
        val injury2 = response.filter { it.id == id2 }.first()

        assertEquals(2, response.count())

        assertEquals(id1, injury1.id)
        assertEquals(description1, injury1.description)
        assertEquals(occurredAt1, injury1.occurredAt)

        assertEquals(id2, injury2.id)
        assertEquals(description2, injury2.description)
        assertEquals(occurredAt2, injury2.occurredAt)
    }

    @Test
    fun `Delete an injury`() {
        // Given
        val description = "description"
        val occurredAt = LocalDateTime.now()
        val id = createInjury(description, occurredAt).id

        // When
        val response = injuryClient.deleteInjury(id, authorization)

        // Then
        assertEquals(200, response.status.code)
    }

    @Test
    fun `Post an image to an injury`() {
        // Given
        val description = "description"
        val occurredAt = LocalDateTime.now()
        val id = createInjury(description, occurredAt).id

        val path = "src/test/resources/injury_image.png"
        val file = File(path)
        val requestBody = MultipartBody.builder()
                .addPart("file",
                        file.name,
                        MediaType.APPLICATION_OCTET_STREAM_TYPE,
                        file
                ).build()

        // When
        val response = injuryClient.postImage(id, requestBody, authorization)

        // Then
        assertEquals("$id:${file.name}", response.key)
    }

    @Test
    fun `Get an image of an injury`() {
        // Given
        val description = "description"
        val occurredAt = LocalDateTime.now()
        val injuryId = createInjury(description, occurredAt).id

        val path = "src/test/resources/injury_image.png"
        val imageId = createInjuryImage(path, injuryId).id

        //When
        val response = injuryClient.getImage(injuryId, imageId, authorization)

        // Then
        val expectedBytes = File(path).readBytes()
        val actualBytes = response.body() as ByteArray

        assertEquals(expectedBytes.size, actualBytes.size)

        var equal = false
        expectedBytes.forEachIndexed { index, byte -> equal = byte == actualBytes[index] }
        assertTrue(equal)
    }

    @Test
    fun `Delete an image of an injury`() {
        // Given
        val description = "description"
        val occurredAt = LocalDateTime.now()
        val injuryId = createInjury(description, occurredAt).id

        val path = "src/test/resources/injury_image.png"
        val imageId = createInjuryImage(path, injuryId).id

        //When
        val response = injuryClient.deleteImage(injuryId, imageId, authorization)

        // Then
        assertEquals(200, response.status.code)
    }

    private fun createInjury(description: String, occurredAt: LocalDateTime): InjuryResponse {
        val request = InjuryRequest(description, occurredAt)
        return injuryClient.postInjury(request, authorization)
    }

    private fun createInjuryImage(path: String, injuryId: Long): ImageReference {
        val file = File(path)
        val requestBody = MultipartBody.builder()
                .addPart("file",
                        file.name,
                        MediaType.APPLICATION_OCTET_STREAM_TYPE,
                        file
                ).build()
        return injuryClient.postImage(injuryId, requestBody, authorization)
    }
}
