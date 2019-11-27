package dk.fitfit.injurylog.configuration

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import io.micronaut.context.annotation.Value
import javax.inject.Singleton

@Singleton
class AwsConfiguration(@Value("\${aws.key}") private val key: String,
                       @Value("\${aws.secret}") private val secret: String,
                       @Value("\${aws.s3.bucket}") val bucket: String,
                       @Value("\${aws.s3.region}") val region: String,
                       @Value("\${aws.s3.multipart-upload-threshold}") val multipartUploadThreshold: Long,
                       @Value("\${aws.s3.max-upload-threads}") val maxUploadThreads: Int) : AWSCredentialsProvider {
    override fun getCredentials(): AWSCredentials = BasicAWSCredentials(key, secret)

    override fun refresh() {
    }
}
