package dk.fitfit.injurylog.service

import io.micronaut.http.multipart.CompletedFileUpload
import java.io.InputStream

interface FileStorage {
    fun put(key: String, file: CompletedFileUpload): String?
    fun get(key: String): InputStream
    fun delete(key: String)
}
