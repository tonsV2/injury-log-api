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

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        roleRepository.deleteAll()
    }

    @Test
    fun findRole() {
        roleRepository.save(Role(Role.ADMIN))

        val role = roleRepository.findByName(Role.ADMIN)

        assertNotNull(role)
    }

    @Test
    fun findRole_notFound() {
        val role = roleRepository.findByName(Role.ADMIN)

        assertNull(role)
    }
}
