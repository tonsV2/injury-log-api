package dk.fitfit.injurylog.service.impl

import com.amazonaws.AmazonServiceException
import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.S3ObjectInputStream
import dk.fitfit.injurylog.configuration.AwsConfiguration
import dk.fitfit.injurylog.service.FileStorageService
import io.micronaut.http.MediaType
import io.micronaut.http.multipart.CompletedFileUpload
import io.micronaut.test.annotation.MicronautTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.apache.http.client.methods.HttpGet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.util.*
import javax.inject.Inject
import kotlin.test.assertFailsWith

@MicronautTest
internal class FileStorageServiceImplTest {
    private lateinit var fileStorageService: FileStorageService

    @Inject
    private lateinit var awsConfiguration: AwsConfiguration

    private val s3Client = mockk<AmazonS3Client>()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        fileStorageService = FileStorageServiceImpl(awsConfiguration, s3Client)
    }

    @Test
    fun put() {
        val file = mockk<CompletedFileUpload>()
        val key = "key"
        val fileData = "test data"
        val inputStream = ByteArrayInputStream(fileData.toByteArray())
        every { file.inputStream } returns inputStream
        every { file.contentType } returns Optional.of(MediaType.APPLICATION_OCTET_STREAM_TYPE)
        every { file.size } returns fileData.length.toLong()
        every { s3Client.putObject(any()) } returns mockk()

        fileStorageService.put(key, file)

        verify(exactly = 1) { file.inputStream }
        verify(exactly = 1) { s3Client.putObject(any()) }
        verify(exactly = 1) { file.contentType }
        verify(atLeast = 1) { file.size }
    }

    @Test
    fun put_imageNotAdded_because_sdkClientException() {
        val file = mockk<CompletedFileUpload>()
        val key = "key"
        val fileData = "test data"
        val inputStream = ByteArrayInputStream(fileData.toByteArray())
        every { file.inputStream } returns inputStream
        every { file.contentType } returns Optional.of(MediaType.APPLICATION_OCTET_STREAM_TYPE)
        every { file.size } returns fileData.length.toLong()
        val exceptionMessage = "exception message"
        every { s3Client.putObject(any()) } throws SdkClientException(exceptionMessage)

        val exception = assertFailsWith<ImageNotAddedException> {
            fileStorageService.put(key, file)
        }

        assert(exception.message?.contains(exceptionMessage) == true)
        verify(exactly = 1) { s3Client.putObject(any()) }
    }

    @Test
    fun put_imageNotAdded_because_amazonServiceException() {
        val file = mockk<CompletedFileUpload>()
        val key = "key"
        val fileData = "test data"
        val inputStream = ByteArrayInputStream(fileData.toByteArray())
        every { file.inputStream } returns inputStream
        every { file.contentType } returns Optional.of(MediaType.APPLICATION_OCTET_STREAM_TYPE)
        every { file.size } returns fileData.length.toLong()
        val exceptionMessage = "exception message"
        every { s3Client.putObject(any()) } throws AmazonServiceException(exceptionMessage)

        val exception = assertFailsWith<ImageNotAddedException> {
            fileStorageService.put(key, file)
        }
        println(exception.message)

        assert(exception.message?.contains(exceptionMessage) == true)

        verify(exactly = 1) { s3Client.putObject(any()) }
    }

    @Test
    fun get() {
        val key = "key"
        val getObjectRequest = GetObjectRequest(awsConfiguration.bucket, key)
        val fileData = "test data"
        val inputStream = ByteArrayInputStream(fileData.toByteArray())
        every { s3Client.getObject(getObjectRequest).objectContent } returns S3ObjectInputStream(inputStream, HttpGet())

        val s3ObjectInputStream = fileStorageService.get(key)

        val content = s3ObjectInputStream.bufferedReader().use(BufferedReader::readText)
        assertEquals(fileData, content)
        verify(exactly = 1) { s3Client.getObject(getObjectRequest).objectContent }
    }

    @Test
    fun delete() {
        val key = "key"
        every { s3Client.deleteObject(awsConfiguration.bucket, key) } returns mockk()

        fileStorageService.delete(key)

        verify(exactly = 1) { s3Client.deleteObject(awsConfiguration.bucket, key) }
    }

    // TODO: This seems a bit... Limited
    @Test
    fun s3ClientFactory() {
        val s3ClientFactory = S3ClientFactory()

        val s3client = s3ClientFactory.s3client(awsConfiguration)

        assertTrue(s3Client is AmazonS3)
    }
}
