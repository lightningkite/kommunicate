package com.lightningkite.kommunicate


suspend fun HttpClient.callString(
    url: String,
    method: HttpMethod,
    body: HttpBody = HttpBody.EMPTY,
    headers: Map<String, List<String>> = mapOf()
): String = callStringDetail(url, method, body, headers).let {
    if (it.failure != null) throw it.failure
    else it.result!!
}

suspend fun HttpClient.callByteArray(
    url: String,
    method: HttpMethod,
    body: HttpBody = HttpBody.EMPTY,
    headers: Map<String, List<String>> = mapOf()
): ByteArray = callByteArrayDetail(url, method, body, headers).let {
    if (it.failure != null) throw it.failure
    else it.result!!
}
