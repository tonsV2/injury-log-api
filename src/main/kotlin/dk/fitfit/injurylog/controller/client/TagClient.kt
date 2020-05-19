package dk.fitfit.injurylog.controller.client

import dk.fitfit.injurylog.dto.TagRequest
import dk.fitfit.injurylog.dto.TagResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("/")
interface TagClient {
    @Get("/tags/updates")
    fun findUpdates(@QueryValue updatedTimestamp: Long, @Header authorization: String): List<TagResponse>

    @Post("/tags")
    fun postTag(tagRequest: TagRequest, @Header authorization: String): HttpResponse<TagResponse>

    @Get("/tags/{name}")
    fun getTagsStartingWith(name: String, @Header authorization: String): HttpResponse<List<TagResponse>>
}
