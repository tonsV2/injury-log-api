package dk.fitfit.injurylog.service.impl

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.transfer.TransferManager
import com.amazonaws.services.s3.transfer.TransferManagerBuilder
import dk.fitfit.injurylog.configuration.AwsConfiguration
import dk.fitfit.injurylog.service.FileStorage
import io.micronaut.http.MediaType
import io.micronaut.http.multipart.CompletedFileUpload
import io.micronaut.http.multipart.FileUpload
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.IOException
import java.net.URL
import java.util.concurrent.Executors
import javax.inject.Singleton

// Inspiration: https://github.com/micronaut-guides/micronaut-file-upload/blob/master/complete/src/main/java/example/micronaut/S3FileRepository.java

@Singleton
class S3FileRepository(private val awsConfiguration: AwsConfiguration) : FileStorage, Closeable {
    private val bucketName: String = awsConfiguration.bucket
    private val s3Client: AmazonS3
    private val transferManager: TransferManager

    init {
        s3Client = AmazonS3Client.builder()
                .withRegion(awsConfiguration.region)
                .withCredentials(awsConfiguration)
                .build()

        val multipartUploadThreshold: Long = awsConfiguration.multipartUploadThreshold
        val maxUploadThreads: Int = awsConfiguration.maxUploadThreads
        transferManager = TransferManagerBuilder.standard()
                .withS3Client(s3Client)
                .withMultipartUploadThreshold(multipartUploadThreshold)
                .withExecutorFactory { Executors.newFixedThreadPool(maxUploadThreads) }
                .build()
    }

    override fun put(key: String, file: CompletedFileUpload): String? {
        return try {
            val inputStream = file.inputStream
            val request: PutObjectRequest = PutObjectRequest(bucketName, key, inputStream, createObjectMetadata(file)).withCannedAcl(CannedAccessControlList.PublicRead)
            s3Client.putObject(request)
            inputStream.close()
            key
        } catch (e: IOException) {
            if (LOG.isErrorEnabled) {
                LOG.error("Error occurred while uploading file " + e.message)
            }
            null
        }
    }

    override fun get(key: String): URL = s3Client.getUrl(bucketName, key)

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

    companion object {
        private val LOG = LoggerFactory.getLogger(S3FileRepository::class.java)
    }
}
