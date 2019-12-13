package dk.fitfit.injurylog.service.impl

import dk.fitfit.injurylog.domain.Role
import dk.fitfit.injurylog.repository.RoleRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class RoleServiceImplTest {
    private val roleRepository = mockk<RoleRepository>()
    private val roleService = RoleServiceImpl(roleRepository)

    @Test
    fun `Save role`() {
        val id = 123L
        val name = Role.ADMIN
        val role = Role(name = name, id = id)
        every { roleRepository.save(role) } returns role

        val saved = roleService.save(role)

        assertEquals(id, saved.id)
        assertEquals(name, saved.name)
        verify(exactly = 1) { roleRepository.save(role) }
    }

    @Test
    fun `Find all roles`() {
        val id0 = 123L
        val name0 = Role.ADMIN
        val role0 = Role(name = name0, id = id0)
        val id1 = 123L
        val name1 = Role.ADMIN
        val role1 = Role(name = name1, id = id1)
        every { roleRepository.findAll() } returns listOf(role0, role1)

        val roles = roleService.findAll()

        assertEquals(2, roles.count())
        assertTrue(roles.contains(role0))
        assertTrue(roles.contains(role1))
        verify(exactly = 1) { roleRepository.findAll() }
    }

    @Test
    fun `Get role by name`() {
        val id = 123L
        val name = Role.ADMIN
        val role = Role(name = name, id = id)
        every { roleRepository.findByName(name) } returns role

        val found = roleService.get(name)

        assertEquals(id, found.id)
        assertEquals(name, found.name)
        verify(exactly = 1) { roleRepository.findByName(name) }
    }

    @Test
    fun `Ensure RoleNotFoundException is thrown if role isn't found`() {
        val name = Role.ADMIN
        every { roleRepository.findByName(name) } returns null

        assertThrows(RoleNotFoundException::class.java) {
            roleService.get(name)
        }

        verify(exactly = 1) { roleRepository.findByName(name) }
    }
}
