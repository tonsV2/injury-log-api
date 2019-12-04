package dk.fitfit.injurylog.service.impl

import dk.fitfit.injurylog.domain.Role
import dk.fitfit.injurylog.repository.RoleRepository
import dk.fitfit.injurylog.service.RoleService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

internal class RoleServiceImplTest {
    private lateinit var roleService: RoleService
    private val roleRepository = mockk<RoleRepository>()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        roleService = RoleServiceImpl(roleRepository)
    }

    @Test
    fun save() {
        val id = 123L
        val name = Role.ADMIN
        val role = Role(name = name, id = id)
        every { roleRepository.save(role) } returns role

        val saved = roleService.save(role)

        assertEquals(saved.id, id)
        assertEquals(saved.name, name)
    }

    @Test
    fun findAll() {
        val id0 = 123L
        val name0 = Role.ADMIN
        val role0 = Role(name = name0, id = id0)
        val id1 = 123L
        val name1 = Role.ADMIN
        val role1 = Role(name = name1, id = id1)
        every { roleRepository.findAll() } returns listOf(role0, role1)

        val roles = roleService.findAll()

        assertEquals(roles.count(), 2)
        assertTrue(roles.contains(role0))
        assertTrue(roles.contains(role1))
    }

    @Test
    fun getRole() {
        val id = 123L
        val name = Role.ADMIN
        val role = Role(name = name, id = id)
        every { roleRepository.findByName(name) } returns role

        val found = roleService.getRole(name)

        assertEquals(found.id, id)
        assertEquals(found.name, name)
    }

    @Test
    fun getRole_notFound() {
        val name = Role.ADMIN
        every { roleRepository.findByName(name) } returns null

        assertThrows(RoleNotFoundException::class.java) {
            roleService.getRole(name)
        }
        fail("RoleNotFoundException not thrown")
    }
}