package dk.fitfit.injurylog.repository

import dk.fitfit.injurylog.domain.ImageReference
import dk.fitfit.injurylog.service.FileStorageService
import io.micronaut.aop.Around
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.context.annotation.Type
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import javax.inject.Singleton

@Repository
interface ImageReferenceRepository : CrudRepository<ImageReference, Long> {
    @InterceptImageReferenceDeletion
    override fun delete(entity: ImageReference)
}

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Around
@Type(ImageReferenceDeleteInterceptor::class)
annotation class InterceptImageReferenceDeletion

@Singleton
class ImageReferenceDeleteInterceptor(private val fileStorageService: FileStorageService) : MethodInterceptor<ImageReference, Boolean> {
    override fun intercept(context: MethodInvocationContext<ImageReference, Boolean>): Boolean {
        val imageReference = context.parameterValues[0] as ImageReference
        fileStorageService.delete(imageReference.key)
        context.proceed()
        return true
    }
}
