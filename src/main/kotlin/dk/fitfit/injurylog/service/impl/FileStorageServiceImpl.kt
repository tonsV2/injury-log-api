package dk.fitfit.injurylog.service.impl

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.transfer.TransferManager
import com.amazonaws.services.s3.transfer.TransferManagerBuilder
import dk.fitfit.injurylog.configuration.AwsConfiguration
import dk.fitfit.injurylog.service.FileStorageService
import io.micronaut.http.MediaType
import io.micronaut.http.multipart.CompletedFileUpload
import io.micronaut.http.multipart.FileUpload
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.Executors
import javax.inject.Singleton

// Inspiration: https://github.com/micronaut-guides/micronaut-file-upload/blob/master/complete/src/main/java/example/micronaut/S3FileRepository.java

@Singleton
class FileStorageServiceImpl(private val awsConfiguration: AwsConfiguration) : FileStorageService, Closeable {
    private val bucketName: String = awsConfiguration.bucket
    private val s3Client: AmazonS3 = AmazonS3Client.builder()
            .withRegion(awsConfiguration.region)
            .withCredentials(awsConfiguration)
            .build()
    private val transferManager: TransferManager

    init {
        transferManager = TransferManagerBuilder.standard()
                .withS3Client(s3Client)
                .withMultipartUploadThreshold(awsConfiguration.multipartUploadThreshold)
                .withExecutorFactory { Executors.newFixedThreadPool(awsConfiguration.maxUploadThreads) }
                .build()
    }

    override fun put(key: String, file: CompletedFileUpload) {
        return try {
            val inputStream = file.inputStream
            val request: PutObjectRequest = PutObjectRequest(bucketName, key, inputStream, createObjectMetadata(file))
                    .withCannedAcl(CannedAccessControlList.Private)
            s3Client.putObject(request)
            inputStream.close()
        } catch (e: IOException) {
            throw ImageNotAddedException(e)
        }
    }

//    override fun get(key: String): URL = s3Client.getUrl(bucketName, key)
    override fun get(key: String): InputStream = s3Client.getObject(GetObjectRequest(bucketName, key)).objectContent

    override fun delete(key: String) = s3Client.deleteObject(bucketName, key)

    @Throws(IOException::class)
    override fun close() = transferManager.shutdownNow()

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
