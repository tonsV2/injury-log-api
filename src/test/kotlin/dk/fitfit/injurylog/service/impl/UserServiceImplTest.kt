package dk.fitfit.injurylog.service.impl

import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.repository.UserRepository
import dk.fitfit.injurylog.service.UserService
import io.micronaut.test.annotation.MicronautTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

@MicronautTest
internal open class UserServiceImplTest {
    private lateinit var userService: UserService
    private val userRepository = mockk<UserRepository>()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        userService = UserServiceImpl(userRepository)
    }

    @Test
    fun save() {
        val email = "email"
        val id = 123L
        val user = User(email = email, id = id)
        every { userRepository.save(user) } returns user

        val saved = userService.save(user)

        assertEquals(saved.email, email)
        assertEquals(saved.id, id)
    }

    @Test
    fun getByEmail() {
        val email = "email"
        val id = 666L
        val user = User(email = email, id = id)
        every { userRepository.findByEmail(email) } returns user

        val gotten = userService.getByEmail(email)

        assertEquals(gotten.email, email)
        assertEquals(gotten.id, id)
    }

    @Test
    fun getByEmail_notFound() {
        val email = "email"
        every { userRepository.findByEmail(email) } returns null

        assertThrows(UserNotFoundException::class.java) {
            userService.getByEmail(email)
        }
        fail("UserNotFoundException not thrown")
    }

    @Test
    fun findAll() {
        val user0 = User("email", id = 123L)
        val email1 = "email2"
        val id1 = 124L
        val user1 = User(email = email1, id = id1)
        every { userRepository.findAll() } returns listOf(user0, user1)

        val users = userService.findAll()

        assertEquals(users.count(), 2)
        assertTrue(users.contains(user0))
        assertTrue(users.contains(user1))
    }
}
