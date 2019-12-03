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
    fun findByEmail() {
        userRepository.save(User(email))

        val user = userRepository.findByEmail(email)

        assertNotNull(user)
    }

    @Test
    fun findByEmail_notFound() {
        val user = userRepository.findByEmail(email)

        assertNull(user)
    }
}
