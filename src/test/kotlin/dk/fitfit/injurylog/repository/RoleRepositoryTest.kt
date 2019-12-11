package dk.fitfit.injurylog.repository

import dk.fitfit.injurylog.domain.Role
import io.micronaut.test.annotation.MicronautTest
import io.mockk.MockKAnnotations
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
internal open class RoleRepositoryTest {
    @Inject
    lateinit var roleRepository: RoleRepository

    @Inject
    lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        userRepository.deleteAll()
        roleRepository. deleteAll()
    }

    @Test
    fun `Find role by name`() {
        val name = Role.ADMIN
        roleRepository.save(Role(name))

        val role = roleRepository.findByName(name)

        assertNotNull(role)
    }

    @Test
    fun `Ensure null is returned when a role isn't found`() {
        val name = Role.ADMIN

        val role = roleRepository.findByName(name)

        assertNull(role)
    }
}
