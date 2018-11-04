package com.lightningkite.kommunicate

import kotlinx.io.core.ByteReadPacket

expect object HttpClient {
    suspend fun callStringDetail(
        url: String,
        method: HttpMethod,
        body: HttpBody = HttpBody.EMPTY,
        headers: Map<String, List<String>> = mapOf()
    ): HttpResponse<String>

    suspend fun callByteArrayDetail(
        url: String,
        method: HttpMethod,
        body: HttpBody = HttpBody.EMPTY,
        headers: Map<String, List<String>> = mapOf()
    ): HttpResponse<ByteArray>

    suspend fun callOutputDetail(
        url: String,
        method: HttpMethod,
        body: HttpBody = HttpBody.EMPTY,
        headers: Map<String, List<String>> = mapOf()
    ): HttpResponse<ByteReadPacket>

    suspend fun socketString(
        url: String
    ): HttpWebSocket<String, String>

    suspend fun socketByteArray(
        url: String
    ): HttpWebSocket<ByteArray, ByteArray>
}

