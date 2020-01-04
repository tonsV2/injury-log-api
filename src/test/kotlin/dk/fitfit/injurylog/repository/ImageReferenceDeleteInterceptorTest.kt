package dk.fitfit.injurylog.repository

import dk.fitfit.injurylog.domain.ImageReference
import dk.fitfit.injurylog.service.FileStorageService
import io.micronaut.aop.MethodInvocationContext
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class ImageReferenceDeleteInterceptorTest {
    private val fileStorageService = mockk<FileStorageService>()
    private val methodInvocationContext = mockk<MethodInvocationContext<ImageReference, Boolean>>()

    private val imageReferenceDeleteInterceptor = ImageReferenceDeleteInterceptor(fileStorageService)

    @Test
    fun `Ensure interceptor invokes delete method with correct key`() {
        // Given
        val key = "id:filename"
        val id = 123L
        val imageReference = ImageReference(key, id)
        every { methodInvocationContext.parameterValues } returns arrayOf(imageReference)
        every { methodInvocationContext.proceed() } returns true
        every { fileStorageService.delete(key) } returns Unit

        // When
        imageReferenceDeleteInterceptor.intercept(methodInvocationContext)

        // Then
        verify(exactly = 1) { fileStorageService.delete(key) }
        verify(exactly = 1) { methodInvocationContext.proceed() }
    }
}
