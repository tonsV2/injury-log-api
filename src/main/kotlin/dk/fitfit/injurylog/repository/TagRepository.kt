package dk.fitfit.injurylog.repository

import dk.fitfit.injurylog.domain.Tag
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.time.LocalDateTime

@Repository
interface TagRepository : CrudRepository<Tag, Long> {
    @Query("from Tag t where t.name like concat(:name, '%')")
    fun findTagsStartingWith(name: String): Set<Tag>

    fun findByUpdatedAfter(updatedAfter: LocalDateTime): Set<Tag>
}
