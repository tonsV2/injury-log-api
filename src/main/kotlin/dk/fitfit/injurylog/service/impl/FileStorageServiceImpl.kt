package dk.fitfit.injurylog.service.impl

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList.Private
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import dk.fitfit.injurylog.configuration.AwsConfiguration
import dk.fitfit.injurylog.service.FileStorageService
import io.micronaut.context.annotation.Factory
import io.micronaut.http.MediaType
import io.micronaut.http.multipart.CompletedFileUpload
import io.micronaut.http.multipart.FileUpload
import java.io.IOException
import java.io.InputStream
import javax.inject.Singleton

// Inspiration: https://github.com/micronaut-guides/micronaut-file-upload/blob/master/complete/src/main/java/example/micronaut/S3FileRepository.java

@Factory
class S3ClientFactory {
    @Singleton
    fun s3client(awsConfiguration: AwsConfiguration): AmazonS3 = AmazonS3Client.builder()
            .withRegion(awsConfiguration.region)
            .withCredentials(awsConfiguration)
            .build()
}

@Singleton
class FileStorageServiceImpl(private val awsConfiguration: AwsConfiguration, private val s3Client: AmazonS3) : FileStorageService {
    override fun put(key: String, file: CompletedFileUpload) {
        return try {
            file.inputStream.use {
                val objectMetadata = createObjectMetadata(file)
                val request = PutObjectRequest(awsConfiguration.bucket, key, it, objectMetadata)
                        .withCannedAcl(Private)
                s3Client.putObject(request)
            }
        } catch (e: IOException) {
            throw ImageNotAddedException(e)
        }
    }

    override fun get(key: String): InputStream = s3Client.getObject(GetObjectRequest(awsConfiguration.bucket, key)).objectContent

    override fun delete(key: String) = s3Client.deleteObject(awsConfiguration.bucket, key)

    private fun createObjectMetadata(file: FileUpload): ObjectMetadata {
        val objectMetadata = ObjectMetadata()
        file.contentType.ifPresent { contentType: MediaType -> objectMetadata.contentType = contentType.name }
        if (file.size != 0L) {
            objectMetadata.contentLength = file.size
        }
        return objectMetadata
    }
}

class ImageNotAddedException(t: Throwable) : RuntimeException(t)
