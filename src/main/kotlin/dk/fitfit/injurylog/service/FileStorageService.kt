package dk.fitfit.injurylog.service

import io.micronaut.http.multipart.CompletedFileUpload
import java.io.InputStream

interface FileStorageService {
    fun put(key: String, file: CompletedFileUpload)
    fun get(key: String): InputStream
    fun delete(key: String)
}
