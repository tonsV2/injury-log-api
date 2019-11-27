package dk.fitfit.injurylog.service.impl

import dk.fitfit.injurylog.domain.ImageReference
import dk.fitfit.injurylog.domain.Injury
import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.repository.ImageReferenceRepository
import dk.fitfit.injurylog.repository.InjuryRepository
import dk.fitfit.injurylog.service.FileStorage
import dk.fitfit.injurylog.service.InjuryService
import io.micronaut.http.multipart.CompletedFileUpload
import java.io.InputStream
import javax.inject.Singleton
import javax.transaction.Transactional

@Singleton
@Transactional
class InjuryServiceImpl(private val injuryRepository: InjuryRepository, private val fileStorage: FileStorage, private val imageReferenceRepository: ImageReferenceRepository) : InjuryService {
    override fun save(injury: Injury): Injury = injuryRepository.save(injury)

    override fun findAll(user: User): Iterable<Injury> = injuryRepository.findAll(user)

    override fun addImage(user: User, id: Long, file: CompletedFileUpload): ImageReference? {
        val injury = injuryRepository.findBy(user, id) ?: throw InjuryNotFoundException(user.email, id)
        if (injury.imageReferences.size >= 3) throw TooManyImagesException(injury.imageReferences.size)
        val key = "${injury.id}:${file.filename}"
        return fileStorage.put(key, file)?.let {
            val imageReference = imageReferenceRepository.save(ImageReference(it))
            injury.imageReferences.add(imageReference)
            injuryRepository.save(injury)
            imageReference
        }
    }

    override fun deleteImage(user: User, injuryId: Long, imageId: Long) {
        val injury = injuryRepository.findBy(user, injuryId) ?: throw InjuryNotFoundException(user.email, injuryId)
        if (injury.imageReferences.none { it.id == imageId }) throw ImageReferenceNotFoundException(imageId)
        imageReferenceRepository.findById(imageId).ifPresent {
            fileStorage.delete(it.key)
            injury.imageReferences.remove(it)
            imageReferenceRepository.delete(it)
        }
    }

    override fun getImage(user: User, injuryId: Long, imageId: Long): InputStream {
        val injury = injuryRepository.findBy(user, injuryId) ?: throw InjuryNotFoundException(user.email, injuryId)
        if (injury.imageReferences.none { it.id == imageId }) throw ImageReferenceNotFoundException(imageId)
        val imageReference = imageReferenceRepository.findById(imageId).get()
        return fileStorage.get(imageReference.key)
    }
}

class TooManyImagesException(numberOfImages: Int) : RuntimeException("Too many images added to the injury. Maximum number of images allowed is 3, current number is $numberOfImages")
class InjuryNotFoundException(email: String, id: Long) : RuntimeException("No injury found with: (id: $id, email: $email)")
class ImageReferenceNotFoundException(id: Long) : RuntimeException("No imageReference found with id: $id")
