package dk.fitfit.injurylog.service.impl

import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.repository.UserRepository
import dk.fitfit.injurylog.service.UserService
import io.micronaut.test.annotation.MicronautTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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

        assertEquals(email, saved.email)
        assertEquals(id, saved.id)

        verify(exactly = 1) { userRepository.save(user) }
    }

    @Test
    fun getByEmail() {
        val email = "email"
        val id = 123L
        val user = User(email = email, id = id)
        every { userRepository.findByEmail(email) } returns user

        val gotten = userService.getByEmail(email)

        assertEquals(email, gotten.email)
        assertEquals(id, gotten.id)

        verify(exactly = 1) { userRepository.findByEmail(email) }
    }

    @Test
    fun getByEmail_notFound() {
        val email = "email"
        every { userRepository.findByEmail(email) } returns null

        assertThrows(UserNotFoundException::class.java) {
            userService.getByEmail(email)
        }

        verify(exactly = 1) { userRepository.findByEmail(email) }
    }

    @Test
    fun findAll() {
        val user0 = User("email", id = 123L)
        val email1 = "email2"
        val id1 = 124L
        val user1 = User(email = email1, id = id1)
        every { userRepository.findAll() } returns listOf(user0, user1)

        val users = userService.findAll()

        assertEquals(2, users.count())
        assertTrue(users.contains(user0))
        assertTrue(users.contains(user1))
        verify(exactly = 1) { userRepository.findAll() }
    }
}
