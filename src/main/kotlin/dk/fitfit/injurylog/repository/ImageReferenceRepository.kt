package dk.fitfit.injurylog.repository

import dk.fitfit.injurylog.domain.ImageReference
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface ImageReferenceRepository : CrudRepository<ImageReference, Long>
