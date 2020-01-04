package dk.fitfit.injurylog.repository

import dk.fitfit.injurylog.domain.ImageReference
import dk.fitfit.injurylog.domain.Injury
import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.service.FileStorageService
import io.micronaut.aop.MethodInvocationContext
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.jupiter.api.Test

internal class InjuryDeleteInterceptorTest {
    private val fileStorageService = mockk<FileStorageService>()
    private val methodInvocationContext = mockk<MethodInvocationContext<Injury, Boolean>>()

    private val injuryDeleteInterceptor = InjuryDeleteInterceptor(fileStorageService)

    @Test
    fun `Ensure interceptor invokes delete method with correct key`() {
        // Given
        val key = "id:filename"
        val id = 123L
        val imageReference = ImageReference(key, id)

        val injury = Injury(description = "", user = User(""), imageReferences = mutableListOf(imageReference))

        every { methodInvocationContext.parameterValues } returns arrayOf(injury)
        every { methodInvocationContext.proceed() } returns true
        every { fileStorageService.delete(key) } returns Unit

        // When
        injuryDeleteInterceptor.intercept(methodInvocationContext)

        // Then
        verify(exactly = 1) { fileStorageService.delete(key) }
        verify(exactly = 1) { methodInvocationContext.proceed() }
    }

    @Test
    fun `Ensure interceptor invokes delete method with correct key with multiple image references`() {
        // Given
        val key = "id:filename"
        val id = 123L
        val imageReference = ImageReference(key, id)

        val key2 = "id:filename2"
        val id2 = 321L
        val imageReference2 = ImageReference(key2, id2)

        val injury = Injury(description = "", user = User(""), imageReferences = mutableListOf(imageReference, imageReference2))

        every { methodInvocationContext.parameterValues } returns arrayOf(injury)
        every { methodInvocationContext.proceed() } returns true
        every { fileStorageService.delete(key) } returns Unit
        every { fileStorageService.delete(key2) } returns Unit

        // When
        injuryDeleteInterceptor.intercept(methodInvocationContext)

        // Then
        verifySequence {
            fileStorageService.delete(key)
            fileStorageService.delete(key2)
        }

        verify(exactly = 1) { methodInvocationContext.proceed() }
    }
}
