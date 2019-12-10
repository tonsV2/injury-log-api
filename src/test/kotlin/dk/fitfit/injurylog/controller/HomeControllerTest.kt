package dk.fitfit.injurylog.controller

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
internal class HomeControllerTest {
    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun getUsers() {
        val request: HttpRequest<String> = HttpRequest.GET("/")
        val body = client.toBlocking().retrieve(request)

        assertNotNull(body)
        assertEquals("Hello World", body)
    }
}
