package com.lightningkite.kommunicate

import kotlinx.io.core.ByteReadPacket
import kotlinx.io.core.readBytes
import kotlinx.io.js.responsePacket
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.xhr.ARRAYBUFFER
import org.w3c.xhr.TEXT
import org.w3c.xhr.XMLHttpRequest
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

actual object HttpClient {

    @Suppress("CAST_NEVER_SUCCEEDS")
    inline private fun Int8Array.asByteArray(): ByteArray = this as ByteArray

    @Suppress("CAST_NEVER_SUCCEEDS")
    inline private fun ByteArray.asInt8Array(): Int8Array = this as Int8Array

    actual suspend fun callStringDetail(
        url: String,
        method: HttpMethod,
        body: HttpBody,
        headers: Map<String, List<String>>
    ): HttpResponse<String> = suspendCoroutine { callback ->
        val request = XMLHttpRequest()
        request.responseType = org.w3c.xhr.XMLHttpRequestResponseType.TEXT
        request.addEventListener("load", callback = {
            val result = toHttpResponse(request, XMLHttpRequest::responseText, XMLHttpRequest::responseText)
            callback.resume(result)
        })
        request.addEventListener("error", callback = {
            val result = toHttpResponse(request, XMLHttpRequest::responseText, XMLHttpRequest::responseText)
            callback.resume(result)
        })
        for (headerGroup in headers) {
            for (value in headerGroup.value) {
                request.setRequestHeader(headerGroup.key, value)
            }
        }
        try {
            request.open(method.name, url)
        } catch (e: Throwable) {
            callback.resumeWithException(ConnectionException(e.message ?: "", e))
        }
        request.send(
            when (body) {
                is HttpBody.BString -> body.value
                is HttpBody.BByteArray -> body.value
                is HttpBody.BInput -> body.value.readBytes()
            }
        )
    }

    actual suspend fun callByteArrayDetail(
        url: String,
        method: HttpMethod,
        body: HttpBody,
        headers: Map<String, List<String>>
    ): HttpResponse<ByteArray> = suspendCoroutine { callback ->
        val request = XMLHttpRequest()
        request.responseType = org.w3c.xhr.XMLHttpRequestResponseType.ARRAYBUFFER
        request.addEventListener("load", callback = {
            val result =
                toHttpResponse(request, XMLHttpRequest::responseByteArrayString, XMLHttpRequest::responseByteArray)
            callback.resume(result)
        })
        request.addEventListener("error", callback = {
            val result =
                toHttpResponse(request, XMLHttpRequest::responseByteArrayString, XMLHttpRequest::responseByteArray)
            callback.resume(result)
        })
        for (headerGroup in headers) {
            for (value in headerGroup.value) {
                request.setRequestHeader(headerGroup.key, value)
            }
        }
        try {
            request.open(method.name, url)
        } catch (e: Throwable) {
            callback.resumeWithException(ConnectionException(e.message ?: "", e))
        }
        request.send(
            when (body) {
                is HttpBody.BString -> body.value
                is HttpBody.BByteArray -> body.value
                is HttpBody.BInput -> body.value.readBytes()
            }
        )
    }

    actual suspend fun callOutputDetail(
        url: String,
        method: HttpMethod,
        body: HttpBody,
        headers: Map<String, List<String>>
    ): HttpResponse<ByteReadPacket> = suspendCoroutine { callback ->
        val request = XMLHttpRequest()
        request.responseType = org.w3c.xhr.XMLHttpRequestResponseType.ARRAYBUFFER
        request.addEventListener("load", callback = {
            val result =
                toHttpResponse(request, XMLHttpRequest::responseByteArrayString, XMLHttpRequest::responsePacket)
            callback.resume(result)
        })
        request.addEventListener("error", callback = {
            val result =
                toHttpResponse(request, XMLHttpRequest::responseByteArrayString, XMLHttpRequest::responsePacket)
            callback.resume(result)
        })
        for (headerGroup in headers) {
            for (value in headerGroup.value) {
                request.setRequestHeader(headerGroup.key, value)
            }
        }
        try {
            request.open(method.name, url)
        } catch (e: Throwable) {
            callback.resumeWithException(ConnectionException(e.message ?: "", e))
        }
        request.send(
            when (body) {
                is HttpBody.BString -> body.value
                is HttpBody.BByteArray -> body.value
                is HttpBody.BInput -> body.value.readBytes()
            }
        )
    }


    inline fun <T> toHttpResponse(
        request: XMLHttpRequest,
        getError: (XMLHttpRequest) -> String,
        getResult: (XMLHttpRequest) -> T
    ): HttpResponse<T> {
        return HttpResponse(
            code = request.status.toInt(),
            headers = request.getAllResponseHeaders()
                .trim().split("\r\n")
                .asSequence()
                .mapNotNull {
                    val splitPoint = it.indexOf(':')
                    if (splitPoint == -1) return@mapNotNull null
                    it.substring(0, splitPoint) to it.substring(splitPoint + 2)
                }
                .groupingBy { it.first }
                .fold(
                    initialValueSelector = { _, _ -> ArrayList<String>() },
                    operation = { key: String, acc: ArrayList<String>, ele: Pair<String, String> ->
                        acc.apply { add(ele.second) }
                    }
                ),
            result = if (request.status / 100 == 2) getResult(request) else null,
            failure = if (request.status / 100 != 2) try {
                HttpException(request.status.toInt(), getError(request))
            } catch (e: dynamic) {
                HttpException(request.status.toInt(), e.toString())
            } else null
        )
    }

    actual suspend fun socketString(
        url: String
    ): HttpWebSocket<String, String> = suspendCoroutine { callback ->
        try {
            HttpWebSocketImpl<String>(url, { send(it) }, { it as String }, {
                callback.resumeWith(it)
            })
        } catch (e: Exception) {
            println(e.message)
            callback.resumeWithException(ConnectionException(e.message ?: "", e))
        }
    }


    actual suspend fun socketByteArray(
        url: String
    ): HttpWebSocket<ByteArray, ByteArray> = suspendCoroutine { callback ->
        try {
            HttpWebSocketImpl<ByteArray>(
                url,
                { send(it.asInt8Array().buffer) },
                { Int8Array(it as ArrayBuffer).asByteArray() },
                {
                    callback.resumeWith(it)
                })
        } catch (e: Exception) {
            println(e.message)
            callback.resumeWithException(ConnectionException(e.message ?: "", e))
        }
    }
}

private fun XMLHttpRequest.responseByteArray(): ByteArray {
    val base = Int8Array(response as ArrayBuffer)
    return ByteArray(base.length) { base[it] }
}

private fun XMLHttpRequest.responseByteArrayString(): String {
    val buffer = response as ArrayBuffer
    val array = Uint8Array(buffer)
    val builder = StringBuilder(capacity = buffer.byteLength)
    for (i in 0 until array.length) {
        builder.append(array[i].toChar())
    }
    return builder.toString()
}
