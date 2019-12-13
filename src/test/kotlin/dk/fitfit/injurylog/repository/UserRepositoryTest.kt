package dk.fitfit.injurylog.repository

import dk.fitfit.injurylog.domain.User
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
internal open class UserRepositoryTest {
    private val email = "email"

    @Inject
    lateinit var userRepository: UserRepository

    @Test
    fun `Find user by email`() {
        userRepository.save(User(email))

        val user = userRepository.findByEmail(email)

        assertNotNull(user)
    }

    @Test
    fun `Ensure null is returned when a user isn't found`() {
        val user = userRepository.findByEmail(email)

        assertNull(user)
    }
}
