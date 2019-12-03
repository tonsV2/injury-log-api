package dk.fitfit.injurylog.repository

import dk.fitfit.injurylog.domain.User
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
internal open class UserRepositoryTest {
    private val email = "email"

    @Inject
    lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        userRepository.save(User(email))
    }

    @Test
    fun findByEmail() {
        val user = userRepository.findByEmail(email)
        Assertions.assertNotNull(user)
    }
}
