package dk.fitfit.injurylog.repository

import dk.fitfit.injurylog.domain.Injury
import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.service.FileStorageService
import io.micronaut.aop.Around
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.context.annotation.Type
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.time.LocalDateTime
import javax.inject.Singleton

@Repository
interface InjuryRepository : CrudRepository<Injury, Long> {
    fun findByUpdatedAfter(updatedAfter: LocalDateTime): Set<Injury>

    @Query("from Injury i where i.user = :user")
    fun findAll(user: User): Iterable<Injury>

    @InterceptInjuryDeletion
    override fun delete(entity: Injury)
}

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Around
@Type(InjuryDeleteInterceptor::class)
annotation class InterceptInjuryDeletion

@Singleton
class InjuryDeleteInterceptor(private val fileStorageService: FileStorageService) : MethodInterceptor<Injury, Boolean> {
    override fun intercept(context: MethodInvocationContext<Injury, Boolean>): Boolean {
        val injury = context.parameterValues[0] as Injury
        injury.imageReferences.forEach {
            fileStorageService.delete(it.key)
        }
        context.proceed()
        return true
    }
}
