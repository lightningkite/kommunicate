package com.lightningkite.kommunicate

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

actual object HttpClient {

    actual suspend fun callStringDetail(
        url: String,
        method: HttpMethod,
        body: HttpBody,
        headers: Map<String, List<String>>
    ): HttpResponse<String> = suspendCoroutine { callback ->

    }

    actual suspend fun callByteArrayDetail(
        url: String,
        method: HttpMethod,
        body: HttpBody,
        headers: Map<String, List<String>>
    ): HttpResponse<ByteArray> = suspendCoroutine { callback ->

    }

    actual suspend fun callOutputDetail(
        url: String,
        method: HttpMethod,
        body: HttpBody,
        headers: Map<String, List<String>>
    ): HttpResponse<ByteReadPacket> = suspendCoroutine { callback ->

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