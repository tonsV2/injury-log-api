package dk.fitfit.injurylog.service

import dk.fitfit.injurylog.domain.ImageReference
import dk.fitfit.injurylog.domain.Injury
import dk.fitfit.injurylog.domain.User
import io.micronaut.http.multipart.CompletedFileUpload

interface InjuryService {
    fun save(injury: Injury): Injury
    fun findAll(user: User): Iterable<Injury>
    fun addImage(user: User, id: Long, file: CompletedFileUpload): ImageReference?
}
