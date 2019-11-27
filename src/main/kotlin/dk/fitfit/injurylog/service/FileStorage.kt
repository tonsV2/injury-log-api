package dk.fitfit.injurylog.service

import io.micronaut.http.multipart.CompletedFileUpload
import java.net.URL

interface FileStorage {
    fun put(key: String, file: CompletedFileUpload): String?
    fun get(key: String): URL
    fun delete(key: String)
}
