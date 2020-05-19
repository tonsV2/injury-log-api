package dk.fitfit.injurylog.service

import dk.fitfit.injurylog.domain.ImageReference
import dk.fitfit.injurylog.domain.Injury
import dk.fitfit.injurylog.domain.User
import io.micronaut.http.multipart.CompletedFileUpload
import java.io.InputStream
import java.time.LocalDateTime

interface InjuryService {
    fun findUpdates(updatedAfter: LocalDateTime): Set<Injury>
    fun save(injury: Injury): Injury
    fun findAll(user: User): Iterable<Injury>
    fun get(user: User, injuryId: Long): Injury
    fun delete(user: User, injuryId: Long)
    fun addImage(user: User, injuryId: Long, file: CompletedFileUpload): ImageReference
    fun deleteImage(user: User, injuryId: Long, imageId: Long)
    fun getImage(user: User, injuryId: Long, imageId: Long): InputStream
}
