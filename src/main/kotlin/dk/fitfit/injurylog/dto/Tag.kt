package dk.fitfit.injurylog.dto

import dk.fitfit.injurylog.domain.Tag

class TagResponse(val name: String, val id: Long)
fun Tag.toTagResponse() = TagResponse(name, id)

class TagRequest(val name: String, val id: Long = 0)
fun TagRequest.toTag() = Tag(name, id)
