package dk.fitfit.injurylog.service.impl

import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.repository.UserRepository
import dk.fitfit.injurylog.service.UserService
import io.micronaut.test.annotation.MicronautTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

@MicronautTest
internal open class UserServiceImplTest {
    private lateinit var userService: UserService

    private val userRepository: UserRepository = mockk(relaxUnitFun = true)


    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        userService = UserServiceImpl(userRepository)
    }

    @Test
    fun save() {
        val email = "email"
        val id = 666L
        val user = User(email)
        every { userRepository.save(any<User>()) } returns User(email = email, id = id)

        val saved = userService.save(user)

        Assertions.assertEquals(saved.email, email)
        Assertions.assertEquals(saved.id, id)
    }

    @Test
    fun getByEmail() {
        val email = "email"
        val id = 666L
        every { userRepository.findByEmail(email) } returns User(email = email, id = id)

        val user = userService.getByEmail(email)

        Assertions.assertEquals(user.email, email)
        Assertions.assertEquals(user.id, id)
    }

    @Test
    fun getByEmail_notFound() {
        val email = "email"
        every { userRepository.findByEmail(email) } throws UserNotFoundException(email)

        try {
            userService.getByEmail(email)
        } catch (e: UserNotFoundException) {
            return
        }
        fail("UserNotFoundException not thrown")
    }

    @Test
    fun findAll() {
        val user0 = User("email", id = 666L)
        val email1 = "email2"
        val id1 = 667L
        val user1 = User(email = email1, id = id1)
        every { userRepository.findAll() } returns listOf(user0, user1)

        val users = userService.findAll()

        Assertions.assertTrue(users.contains(user0))
        Assertions.assertTrue(users.contains(user1))
    }
}
