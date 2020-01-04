package dk.fitfit.injurylog.controller.client

import dk.fitfit.injurylog.domain.ImageReference
import dk.fitfit.injurylog.dto.InjuryRequest
import dk.fitfit.injurylog.dto.InjuryResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.multipart.MultipartBody

@Client("/")
interface InjuryClient {
    @Post("/injuries")
    fun postInjury(injuryRequest: InjuryRequest, @Header authorization: String): HttpResponse<InjuryResponse>

    @Get("/injuries/{id}")
    fun getInjury(id: Long, @Header authorization: String): HttpResponse<InjuryResponse>

    @Get("/injuries")
    fun getInjuries(@Header authorization: String): HttpResponse<Iterable<InjuryResponse>>

    @Delete("/injuries/{id}")
    fun deleteInjury(id: Long, @Header authorization: String): HttpResponse<String?>

    @Post("/injuries/{id}/images", produces = [MediaType.MULTIPART_FORM_DATA])
    fun postImage(id: Long, @Body body: MultipartBody, @Header authorization: String): HttpResponse<ImageReference>

    @Get("/injuries/{injuryId}/images/{imageId}")
    fun getImage(injuryId: Long, imageId: Long, @Header authorization: String): HttpResponse<ByteArray>

    @Delete("/injuries/{injuryId}/images/{imageId}")
    fun deleteImage(injuryId: Long, imageId: Long, @Header authorization: String): HttpResponse<Unit>
}
