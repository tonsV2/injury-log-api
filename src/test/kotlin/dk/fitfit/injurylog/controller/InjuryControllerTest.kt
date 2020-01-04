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
        val injuryResponse = response.body.get()
        assertEquals(description, injuryResponse.description)
        assertEquals(occurredAt, injuryResponse.occurredAt)
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
        val injuryResponse = response.body.get()

        assertEquals(id, injuryResponse.id)
        assertEquals(description, injuryResponse.description)
        assertEquals(occurredAt, injuryResponse.occurredAt)
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
        val injuries = response.body.get()
        val firstInjury = injuries.elementAt(0)
        val secondInjury = injuries.elementAt(1)

        assertEquals(2, injuries.count())

        assertEquals(id2, firstInjury.id)
        assertEquals(description2, firstInjury.description)
        assertEquals(occurredAt2, firstInjury.occurredAt)

        assertEquals(id1, secondInjury.id)
        assertEquals(description1, secondInjury.description)
        assertEquals(occurredAt1, secondInjury.occurredAt)

        val comparison = firstInjury.occurredAt.compareTo(secondInjury.occurredAt)
        assertEquals(1, comparison)
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
    fun `Delete an injury with a single image`() {
        // Given
        val description = "description"
        val occurredAt = LocalDateTime.now()
        val injuryId = createInjury(description, occurredAt).id
        val path = "src/test/resources/injury_image.png"
        createInjuryImage(path, injuryId).id

        // When
        val response = injuryClient.deleteInjury(injuryId, authorization)

        // Then
        assertEquals(200, response.status.code)
    }

    @Test
    fun `Delete an injury with multiple images`() {
        // Given
        val description = "description"
        val occurredAt = LocalDateTime.now()
        val injuryId = createInjury(description, occurredAt).id

        val path = "src/test/resources/injury_image.png"
        val imageId = createInjuryImage(path, injuryId).id

        val path2 = "src/test/resources/injury_image2.png"
        val imageId2 = createInjuryImage(path2, injuryId).id

        // When
        val response = injuryClient.deleteInjury(injuryId, authorization)

        // Then
        assertEquals(200, response.status.code)

        val getImageResponse = injuryClient.getImage(injuryId, imageId, authorization)
        assertEquals(404, getImageResponse.status.code)
        val getImageResponse2 = injuryClient.getImage(injuryId, imageId2, authorization)
        assertEquals(404, getImageResponse2.status.code)
        val getInjuryResponse = injuryClient.getInjury(injuryId, authorization)
        assertEquals(404, getInjuryResponse.status.code)
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
        assertEquals("$id:${file.name}", response.body.get().key)
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
        return injuryClient.postInjury(request, authorization).body.get()
    }

    private fun createInjuryImage(path: String, injuryId: Long): ImageReference {
        val file = File(path)
        val requestBody = MultipartBody.builder()
                .addPart("file",
                        file.name,
                        MediaType.APPLICATION_OCTET_STREAM_TYPE,
                        file
                ).build()
        return injuryClient.postImage(injuryId, requestBody, authorization).body.get()
    }
}
