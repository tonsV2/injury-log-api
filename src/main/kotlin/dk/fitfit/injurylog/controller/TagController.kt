package dk.fitfit.injurylog.controller

import dk.fitfit.injurylog.dto.TagRequest
import dk.fitfit.injurylog.dto.TagResponse
import dk.fitfit.injurylog.dto.toTag
import dk.fitfit.injurylog.dto.toTagResponse
import dk.fitfit.injurylog.repository.TagRepository
import dk.fitfit.injurylog.service.TagService
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller
class TagController(private val tagService: TagService, private val tagRepository: TagRepository) {
    @Post("/tags")
    fun postTag(tagRequest: TagRequest): TagResponse {
        val tag = tagRequest.toTag()
        tagService.save(tag)
        return tag.toTagResponse()
    }

    @Get("/tags/{name}")
    fun getTagsStartingWith(name: String): List<TagResponse> = tagService.findTagsStartingWith(name).map { it.toTagResponse() }
}
