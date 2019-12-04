package dk.fitfit.injurylog.service.impl

import dk.fitfit.injurylog.domain.Injury
import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.repository.ImageReferenceRepository
import dk.fitfit.injurylog.repository.InjuryRepository
import dk.fitfit.injurylog.service.FileStorageService
import dk.fitfit.injurylog.service.InjuryService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class InjuryServiceImplTest {
    private lateinit var injuryService: InjuryService
    private val injuryRepository = mockk<InjuryRepository>()
    private val fileStorageService = mockk<FileStorageService>()
    private val imageReferenceRepository = mockk<ImageReferenceRepository>()

    private val email = "email"
    private val userId = 123L
    private val user = User(email = email, id = userId)

    private val injuryId = 123L
    private val description = "description"
    private val injury = Injury(description = description, user = user, id = injuryId)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        injuryService = InjuryServiceImpl(injuryRepository, fileStorageService, imageReferenceRepository)
    }

    @Test
    fun save() {
        val description = "description"
        val injury = Injury(description, user, id = injuryId)

        every { injuryRepository.save(injury) } returns injury

        val saved = injuryService.save(injury)

        assertEquals(saved.id, injuryId)
        assertEquals(saved.description, description)
        verify(exactly = 1) { injuryRepository.save(injury) }
    }

    @Test
    fun findAll() {
        val id1 = 123L
        val description1 = "description"
        val injury1 = Injury(description = description1, user = user, id = id1)
        every { injuryRepository.findAll(user) } returns listOf(injury, injury1)

        val injuries = injuryService.findAll(user)

        assertEquals(injuries.count(), 2)
        assertTrue(injuries.contains(injury))
        assertTrue(injuries.contains(injury1))
        verify(exactly = 1) { injuryRepository.findAll(user) }
    }

    @Test
    fun get_notFound() {
        val id = 123L
        every { injuryRepository.findById(id) } returns Optional.ofNullable<Injury>(null)

        assertThrows(InjuryNotFoundException::class.java) {
            injuryService.get(user, id)
        }

        verify(exactly = 1) { injuryRepository.findById(id) }
    }

    @Test
    fun get_injuryDoesNotBelongToUser() {
        every { injuryRepository.findById(injuryId) } returns Optional.of(injury)
        val notInjuryOwnerId = 321L
        val notInjuryOwner = User(email = email, id = notInjuryOwnerId)

        assertThrows(InjuryDoesNotBelongToUserException::class.java) {
            injuryService.get(notInjuryOwner, injuryId)
        }

        verify(exactly = 1) { injuryRepository.findById(injuryId) }
    }

    @Test
    fun get() {
        every { injuryRepository.findById(userId) } returns Optional.of(injury)

        val found = injuryService.get(user, userId)

        assertEquals(found.id, userId)
        assertEquals(found.description, description)
        verify(exactly = 1) { injuryRepository.findById(userId) }
    }

    @Test
    fun delete_notFound() {
        val id = 123L
        every { injuryRepository.findById(id) } returns Optional.ofNullable<Injury>(null)

        assertThrows(InjuryNotFoundException::class.java) {
            injuryService.delete(user, id)
        }

        verify(exactly = 1) { injuryRepository.findById(id) }
    }

    @Test
    fun delete_injuryDoesNotBelongToUser() {
        every { injuryRepository.findById(injuryId) } returns Optional.of(injury)
        val notInjuryOwnerId = 321L
        val notInjuryOwner = User(email = email, id = notInjuryOwnerId)

        assertThrows(InjuryDoesNotBelongToUserException::class.java) {
            injuryService.delete(notInjuryOwner, injuryId)
        }

        verify(exactly = 1) { injuryRepository.findById(injuryId) }
    }

    @Test
    fun delete() {
        every { injuryRepository.findById(userId) } returns Optional.of(injury)
        every { injuryRepository.delete(injury) } returns mockk()

        injuryService.delete(user, injuryId)

        verify(exactly = 1) { injuryRepository.findById(userId) }
        verify(exactly = 1) { injuryRepository.delete(injury) }
    }

    @Test
    fun addImage() {
        TODO("Not implemented yet")
    }

    @Test
    fun deleteImage() {
        TODO("Not implemented yet")
    }

    @Test
    fun getImage() {
        TODO("Not implemented yet")
    }
}