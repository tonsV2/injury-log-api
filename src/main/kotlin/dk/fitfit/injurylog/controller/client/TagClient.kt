package dk.fitfit.injurylog.controller.client

import dk.fitfit.injurylog.dto.TagRequest
import dk.fitfit.injurylog.dto.TagResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client("/")
interface TagClient {
    @Post("/tags")
    fun postTag(tagRequest: TagRequest, @Header authorization: String): TagResponse

    @Get("/tags/{name}")
    fun getTagsStartingWith(name: String, @Header authorization: String): List<TagResponse>
}
