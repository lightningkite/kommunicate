package com.lightningkite.kommunicate

import kotlinx.io.core.ByteReadPacket


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

suspend fun HttpClient.callOutput(
    url: String,
    method: HttpMethod,
    body: HttpBody = HttpBody.EMPTY,
    headers: Map<String, List<String>> = mapOf()
): ByteReadPacket = callOutputDetail(url, method, body, headers).let {
    if (it.failure != null) throw it.failure
    else it.result!!
}