package dk.fitfit.injurylog.service

import dk.fitfit.injurylog.domain.ImageReference
import dk.fitfit.injurylog.domain.Injury
import dk.fitfit.injurylog.domain.User
import io.micronaut.http.multipart.CompletedFileUpload
import java.io.InputStream

interface InjuryService {
    fun save(injury: Injury): Injury
    fun findAll(user: User): Iterable<Injury>
    fun get(user: User, injuryId: Long): Injury
    fun addImage(user: User, injuryId: Long, file: CompletedFileUpload): ImageReference?
    fun deleteImage(user: User, injuryId: Long, imageId: Long)
    fun getImage(user: User, injuryId: Long, imageId: Long): InputStream
}
