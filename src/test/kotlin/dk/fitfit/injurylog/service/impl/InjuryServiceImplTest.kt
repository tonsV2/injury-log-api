package dk.fitfit.injurylog.service.impl

import dk.fitfit.injurylog.domain.ImageReference
import dk.fitfit.injurylog.domain.Injury
import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.repository.ImageReferenceRepository
import dk.fitfit.injurylog.repository.InjuryRepository
import dk.fitfit.injurylog.service.FileStorageService
import io.micronaut.http.multipart.CompletedFileUpload
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.util.*

internal class InjuryServiceImplTest {
    private val injuryRepository = mockk<InjuryRepository>()
    private val fileStorageService = mockk<FileStorageService>()
    private val imageReferenceRepository = mockk<ImageReferenceRepository>()

    private val email = "email"
    private val userId = 123L
    private val user = User(email = email, id = userId)

    private val injuryId = 123L
    private val description = "description"
    private val imageReferences = mockk<MutableList<ImageReference>>()
    private val injury = Injury(description = description, user = user, id = injuryId, imageReferences = imageReferences)

    private val injuryService = InjuryServiceImpl(injuryRepository, fileStorageService, imageReferenceRepository)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
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
        // Given
        every { injuryRepository.findById(userId) } returns Optional.of(injury)

        val file = mockk<CompletedFileUpload>()
        val fileName = "file name"
        every { file.filename } returns fileName

        every { fileStorageService.put(any(), file) } returns mockk()

        val key = "${injury.id}:${file.filename}"
        val imageReference = ImageReference(key, injuryId)
        every { imageReferenceRepository.save(ofType(ImageReference::class)) } returns imageReference

        every { injuryRepository.save(injury) } returns injury

        every { injury.imageReferences.size } returns 1
        every { injury.imageReferences.add(ofType(ImageReference::class)) } returns true

        // When
        val imageReferenceSaved = injuryService.addImage(user, injuryId, file)

        // Then
        assertEquals(key, imageReferenceSaved.key)
        val slot = slot<ImageReference>()
        verifyAll {
            injuryRepository.findById(injuryId)
            fileStorageService.put(key, file)
            imageReferenceRepository.save(capture(slot))
            injuryRepository.save(injury)
        }
        assertEquals(imageReference.key, slot.captured.key)
    }

    @Test
    fun addImage_tooManyImages() {
        val file = mockk<CompletedFileUpload>()
        every { injuryRepository.findById(userId) } returns Optional.of(injury)
        every { injury.imageReferences.size } returns 4

        assertThrows(TooManyImagesException::class.java) {
            injuryService.addImage(user, injuryId, file)
        }
    }

    @Test
    fun deleteImage() {
        // Given
        val imageId = 123L
        every { injuryRepository.findById(userId) } returns Optional.of(injury)
        val filename = "filename"
        val key = "${injury.id}:$filename"
        val imageReference = ImageReference(key, imageId)
        every { imageReferenceRepository.save(ofType(ImageReference::class)) } returns imageReference
        every { injury.imageReferences.isEmpty() } returns false
        every { injury.imageReferences.iterator() } returns mutableListOf(ImageReference(key, 123L)).iterator()
        every { injury.imageReferences.add(ofType(ImageReference::class)) } returns true
        every { imageReferenceRepository.findById(imageId) } returns Optional.of(imageReference)
        every { fileStorageService.delete(key) } returns mockk()

        every { imageReferenceRepository.delete(ofType(ImageReference::class)) } returns mockk()
        every { injury.imageReferences.remove(ofType(ImageReference::class)) } returns true

        injury.imageReferences.add(ImageReference(key, imageId))

        // When
        injuryService.deleteImage(user, injuryId, imageId)

        // Then
        verify(exactly = 1) { fileStorageService.delete(key) }
        verify(exactly = 1) { injury.imageReferences.remove(imageReference) }
        verify(exactly = 1) { imageReferenceRepository.delete(imageReference) }
    }

    @Test
    fun deleteImage_imageReferenceNotFound() {
        // Given
        every { injuryRepository.findById(injuryId) } returns Optional.of(injury)
        val notImageId = 321L
        val imageId = 123L
        val filename = "filename"
        val key = "${injury.id}:$filename"
        every { injury.imageReferences.isEmpty() } returns false
        every { injury.imageReferences.iterator() } returns mutableListOf(ImageReference(key, imageId)).iterator()

        // When
        assertThrows(ImageReferenceNotFoundException::class.java) {
            injuryService.deleteImage(user, injuryId, notImageId)
        }
    }

    @Test
    fun getImage() {
        // Given
        val imageId = 123L
        val filename = "filename"
        val key = "${injury.id}:$filename"
        every { injuryRepository.findById(userId) } returns Optional.of(injury)
        every { injury.imageReferences.isEmpty() } returns false
        every { injury.imageReferences.iterator() } returns mutableListOf(ImageReference(key, 123L)).iterator()
        val imageReference = ImageReference(key, imageId)
        every { imageReferenceRepository.findById(imageId) } returns Optional.of(imageReference)
        val fileData = "test data"
        val inputStream = ByteArrayInputStream(fileData.toByteArray())
        every { fileStorageService.get(key) } returns inputStream

        // When
        val inputStreamReturned = injuryService.getImage(user, injuryId, imageId)

        // Then
        val content = inputStreamReturned.bufferedReader().use(BufferedReader::readText)
        assertEquals(fileData, content)

        val imageIdSlot = slot<Long>()
        verify(exactly = 1) { imageReferenceRepository.findById(capture(imageIdSlot)) }
        assertEquals(imageReference.id, imageIdSlot.captured)

        val keySlot = slot<String>()
        verify(exactly = 1) { fileStorageService.get(capture(keySlot)) }
        assertEquals(key, keySlot.captured)
    }

    @Test
    fun getImage_imageReferenceNotFound() {
        every { injuryRepository.findById(injuryId) } returns Optional.of(injury)
        val notImageId = 321L
        val imageId = 123L
        val filename = "filename"
        val key = "${injury.id}:$filename"
        every { injury.imageReferences.isEmpty() } returns false
        every { injury.imageReferences.iterator() } returns mutableListOf(ImageReference(key, imageId)).iterator()

        assertThrows(ImageReferenceNotFoundException::class.java) {
            injuryService.getImage(user, injuryId, notImageId)
        }
    }
}
