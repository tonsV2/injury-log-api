package dk.fitfit.injurylog.service.impl

import dk.fitfit.injurylog.domain.ImageReference
import dk.fitfit.injurylog.domain.Injury
import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.repository.ImageReferenceRepository
import dk.fitfit.injurylog.repository.InjuryRepository
import dk.fitfit.injurylog.service.FileStorageService
import dk.fitfit.injurylog.service.InjuryService
import io.micronaut.http.multipart.CompletedFileUpload
import java.io.InputStream
import javax.inject.Singleton
import javax.transaction.Transactional

@Singleton
@Transactional
class InjuryServiceImpl(private val injuryRepository: InjuryRepository,
                        private val fileStorageService: FileStorageService,
                        private val imageReferenceRepository: ImageReferenceRepository) : InjuryService {
    override fun save(injury: Injury): Injury = injuryRepository.save(injury)

    override fun findAll(user: User): Iterable<Injury> = injuryRepository.findAll(user)

    override fun get(user: User, injuryId: Long): Injury {
        val optional = injuryRepository.findById(injuryId)
        if (optional.isEmpty) {
            throw InjuryNotFoundException(injuryId)
        }
        val injury = optional.get()
        if (injury.user.id != user.id) {
            throw InjuryDoesNotBelongToUserException(user, injuryId)
        }
        return injury
    }

    override fun delete(user: User, injuryId: Long) {
        val injury = get(user, injuryId)
        injuryRepository.delete(injury)
    }

    override fun addImage(user: User, injuryId: Long, file: CompletedFileUpload): ImageReference {
        val injury = get(user, injuryId)

        if (injury.imageReferences.size > 3) throw TooManyImagesException(injury.imageReferences.size)
        val key = "${injury.id}:${file.filename}"
        return fileStorageService.put(key, file).let {
            val imageReference = ImageReference(key)
            imageReferenceRepository.save(imageReference)
            injury.imageReferences.add(imageReference)
            injuryRepository.save(injury)
            imageReference
        }
    }

    override fun deleteImage(user: User, injuryId: Long, imageId: Long) {
        val injury = get(user, injuryId)

        if (injury.imageReferences.none { it.id == imageId }) throw ImageReferenceNotFoundException(imageId)
        imageReferenceRepository.findById(imageId).ifPresent {
            fileStorageService.delete(it.key)
            // TODO: Assert remove returns true
            injury.imageReferences.remove(it)
            imageReferenceRepository.delete(it)
        }
    }

    override fun getImage(user: User, injuryId: Long, imageId: Long): InputStream {
        val injury = get(user, injuryId)

        // TODO: Make two exceptions? One for not found in db and one for not found on injury... Or is this redundant in the first place
        if (injury.imageReferences.none { it.id == imageId }) throw ImageReferenceNotFoundException(imageId)
        val optional = imageReferenceRepository.findById(imageId)
        if (optional.isEmpty) throw ImageReferenceNotFoundException(imageId)
        return fileStorageService.get(optional.get().key)
    }
}

class InjuryDoesNotBelongToUserException(user: User, injuryId: Long) : RuntimeException("Injury does not belong to user (${user.id}, $injuryId)")
class TooManyImagesException(numberOfImages: Int) : RuntimeException("Too many images added to the injury. Maximum number of images allowed is 3, current number is $numberOfImages")
class InjuryNotFoundException(id: Long) : RuntimeException("No injury found with id: $id")
class ImageReferenceNotFoundException(id: Long) : RuntimeException("No imageReference found with id: $id")
