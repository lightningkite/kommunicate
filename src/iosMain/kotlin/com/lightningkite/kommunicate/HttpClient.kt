package com.lightningkite.kommunicate

import kotlinx.cinterop.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.io.core.ByteReadPacket
import platform.Foundation.*
import platform.darwin.NSObject

actual object HttpClient {

    private fun NSData.toByteArray(): ByteArray {
        val data: CPointer<ByteVar> = bytes!!.reinterpret()
        return ByteArray(length.toInt()) { index -> data[index] }
    }

    private fun ByteArray.toNSData(): NSData = NSMutableData().apply {
        if (isEmpty()) return@apply
        this@toNSData.usePinned {
            appendBytes(it.addressOf(0), size.toULong())
        }
    }

    actual suspend fun callStringDetail(
            url: String,
            method: HttpMethod,
            body: HttpBody,
            headers: Map<String, List<String>>
    ): HttpResponse<String> = call(
            url = url,
            method = method,
            body = body,
            headers = headers
    ).copy { NSString.create(data = it, encoding = NSUTF8StringEncoding)!! as String }

    actual suspend fun callByteArrayDetail(
            url: String,
            method: HttpMethod,
            body: HttpBody,
            headers: Map<String, List<String>>
    ): HttpResponse<ByteArray> = call(
            url = url,
            method = method,
            body = body,
            headers = headers
    ).copy { it.toByteArray() }

    actual suspend fun callOutputDetail(
            url: String,
            method: HttpMethod,
            body: HttpBody,
            headers: Map<String, List<String>>
    ) = TODO()

    suspend fun call(
            url: String,
            method: HttpMethod,
            body: HttpBody,
            headers: Map<String, List<String>>
    ): HttpResponse<NSData> = suspendCoroutine { callback ->

        val urlObj = NSURL.URLWithString(url)!!

        val data = when(body){
            HttpBody.EMPTY -> null
            is HttpBody.BString -> (body.value as NSString).dataUsingEncoding(NSUTF8StringEncoding)
            is HttpBody.BByteArray -> body.value.toNSData()
            is HttpBody.BInput -> TODO()
        }

        val completionHandler = { data: NSData?, response:NSURLResponse?, error:NSError? ->
            if(error != null){
                callback.resumeWithException(ConnectionException(
                        message = error.localizedDescription + "\n" + error.localizedFailureReason + "\n" + error.localizedRecoverySuggestion,
                        cause = null
                ))
            } else if(response != null && data != null){
                val casted = response as NSHTTPURLResponse
                callback.resume(HttpResponse<NSData>(
                        code = casted.statusCode.toInt(),
                        headers = casted.allHeaderFields.entries.associate { it.key as String to listOf(it.value as String) },
                        result = data
                ))
            } else {
                callback.resumeWithException(ConnectionException(
                        message = "No error or response",
                        cause = null
                ))
            }
        }

        if(data != null) {
            val sessionConfig = NSURLSessionConfiguration.defaultSessionConfiguration
            @Suppress("UNCHECKED_CAST")
            sessionConfig.setHTTPAdditionalHeaders(headers.mapValues { it.value.joinToString(",") } as Map<Any?, *>)
            val session = NSURLSession.sessionWithConfiguration(sessionConfig)

            val request = NSMutableURLRequest(uRL = urlObj, cachePolicy = NSURLRequestReloadIgnoringLocalCacheData, timeoutInterval = 15.0)
            request.HTTPMethod = method.name.toUpperCase()

            session.uploadTaskWithRequest(request = request, fromData = data)
        } else {
            NSURLSession.sharedSession.dataTaskWithURL(url = urlObj, completionHandler = completionHandler)
        }
    }

    actual suspend fun socketString(
            url: String
    ): HttpWebSocket<String, String> = suspendCoroutine { callback ->
    }


    actual suspend fun socketByteArray(
            url: String
    ): HttpWebSocket<ByteArray, ByteArray> = suspendCoroutine { callback ->
    }
}
