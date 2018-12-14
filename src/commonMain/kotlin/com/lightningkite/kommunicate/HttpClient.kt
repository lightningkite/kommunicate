package com.lightningkite.kommunicate

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

    suspend fun socketString(
        url: String
    ): HttpWebSocket<String, String>

    suspend fun socketByteArray(
        url: String
    ): HttpWebSocket<ByteArray, ByteArray>
}

