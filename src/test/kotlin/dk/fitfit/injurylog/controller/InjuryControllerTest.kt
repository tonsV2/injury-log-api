package dk.fitfit.injurylog.controller

import dk.fitfit.injurylog.configuration.AuthenticationConfiguration
import dk.fitfit.injurylog.dto.InjuryRequest
import dk.fitfit.injurylog.dto.InjuryResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MicronautTest
import io.mockk.MockKAnnotations
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@Client("/")
interface InjuryClient {
    @Post("/injuries")
    fun postInjury(injuryRequest: InjuryRequest, @Header authorization: String): InjuryResponse

    @Get("/injuries/{id}")
    fun getInjury(id: Long, @Header authorization: String): InjuryResponse

    @Get("/injuries")
    fun getInjuries(@Header authorization: String): Iterable<InjuryResponse>

    @Delete("/injuries/{id}")
    fun deleteInjury(id: Long, @Header authorization: String): HttpResponse<String?>
}

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
    fun `Post an image an injury`() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Test
    fun `Get an image of an injury`() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Test
    fun `Delete an image of an injury`() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun createInjury(description: String, occurredAt: LocalDateTime): InjuryResponse {
        val request = InjuryRequest(description, occurredAt)
        return injuryClient.postInjury(request, authorization)
    }
}
