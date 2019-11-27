package dk.fitfit.injurylog.service.impl

import dk.fitfit.injurylog.domain.ImageReference
import dk.fitfit.injurylog.domain.Injury
import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.repository.ImageReferenceRepository
import dk.fitfit.injurylog.repository.InjuryRepository
import dk.fitfit.injurylog.service.FileStorage
import dk.fitfit.injurylog.service.InjuryService
import io.micronaut.http.multipart.CompletedFileUpload
import javax.inject.Singleton
import javax.transaction.Transactional

@Singleton
open class InjuryServiceImpl(private val injuryRepository: InjuryRepository, private val fileStorage: FileStorage, private val imageReferenceRepository: ImageReferenceRepository) : InjuryService {
    override fun save(injury: Injury): Injury = injuryRepository.save(injury)

    override fun findAll(user: User): Iterable<Injury> = injuryRepository.findAll(user)

    @Transactional
    override fun addImage(user: User, id: Long, file: CompletedFileUpload): ImageReference? {
        val injury = injuryRepository.findBy(user, id) ?: throw InjuryNotFoundException(user.email, id)
        if (injury.imageReferences.size >= 3) throw TooManyImagesException(injury.imageReferences.size)
        val key = "${user.id}:${file.filename}"
        return fileStorage.put(key, file)?.let {
            val imageReference = imageReferenceRepository.save(ImageReference(it))
            injury.imageReferences.add(imageReference)
            injuryRepository.save(injury)
            imageReference
        }
    }
}

class TooManyImagesException(numberOfImages: Int) : RuntimeException("Too many images added to the injury. Maximum number of images allowed is 3, current number is $numberOfImages")
class InjuryNotFoundException(email: String, id: Long) : RuntimeException("No injury found with: (id: $id, email: $email)")
