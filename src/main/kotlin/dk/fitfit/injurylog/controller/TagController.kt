package dk.fitfit.injurylog.controller

import dk.fitfit.injurylog.domain.Tag
import dk.fitfit.injurylog.dto.TagRequest
import dk.fitfit.injurylog.dto.TagResponse
import dk.fitfit.injurylog.service.TagService
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller
class TagController(private val tagService: TagService) {
    @Get("/tags/updates")
    fun findUpdates(updatedTimestamp: Long): List<TagResponse> {
        // TODO: Why do I have to add one millisecond to make this work?
        val epochMilli = Instant.ofEpochMilli(updatedTimestamp + 1)
        val updatedAfter = LocalDateTime.ofInstant(epochMilli, ZoneOffset.UTC)

        return tagService.findUpdates(updatedAfter).map { it.toTagResponse() }
    }

    @Post("/tags")
    fun postTag(tagRequest: TagRequest): TagResponse {
        val tag = tagRequest.toTag()
        tagService.save(tag)
        return tag.toTagResponse()
    }

    @Get("/tags/{name}")
    fun getTagsStartingWith(name: String): List<TagResponse> = tagService.findTagsStartingWith(name).map { it.toTagResponse() }
}

fun Tag.toTagResponse() = TagResponse(name, id, created, updated)
fun TagRequest.toTag() = Tag(name, id)
