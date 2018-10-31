package com.lightningkite.kommunicate

import kotlinx.io.core.ByteReadPacket
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.StringFormat


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

suspend fun <T> HttpClient.callSerializer(
    url: String,
    method: HttpMethod,
    body: HttpBody = HttpBody.EMPTY,
    headers: Map<String, List<String>> = mapOf(),
    serializer: StringFormat,
    strategy: DeserializationStrategy<T>
): T = callSerializerDetail(url, method, body, headers, serializer, strategy).let {
    if (it.failure != null) throw it.failure
    else it.result!!
}

suspend fun <T> HttpClient.callSerializerDetail(
    url: String,
    method: HttpMethod,
    body: HttpBody = HttpBody.EMPTY,
    headers: Map<String, List<String>> = mapOf(),
    serializer: StringFormat,
    strategy: DeserializationStrategy<T>
) = callStringDetail(
    url,
    method,
    body,
    headers
).copy { serializer.parse(strategy, it) }

suspend fun <T> HttpClient.callSerializer(
    url: String,
    method: HttpMethod,
    body: HttpBody = HttpBody.EMPTY,
    headers: Map<String, List<String>> = mapOf(),
    serializer: BinaryFormat,
    strategy: DeserializationStrategy<T>
): T = callSerializerDetail(url, method, body, headers, serializer, strategy).let {
    if (it.failure != null) throw it.failure
    else it.result!!
}

suspend fun <T> HttpClient.callSerializerDetail(
    url: String,
    method: HttpMethod,
    body: HttpBody = HttpBody.EMPTY,
    headers: Map<String, List<String>> = mapOf(),
    serializer: BinaryFormat,
    strategy: DeserializationStrategy<T>
) = callByteArrayDetail(
    url,
    method,
    body,
    headers
).copy { serializer.load(strategy, it) }