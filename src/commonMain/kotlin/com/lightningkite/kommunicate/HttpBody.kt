package com.lightningkite.kommunicate

import kotlinx.io.core.ByteReadPacket
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.cbor.CBOR
import kotlinx.serialization.json.JSON
import kotlinx.serialization.protobuf.ProtoBuf

sealed class HttpBody {
    companion object {
        val EMPTY = BString("", "")
        fun string(contentType: String, value: String) = BString(contentType, value)
        fun byteArray(contentType: String, value: ByteArray) = BByteArray(contentType, value)
        fun byteReadPacket(contentType: String, value: ByteReadPacket) = BInput(contentType, value)

        fun <T> serialize(
            strategy: SerializationStrategy<T>,
            value: T,
            json: JSON = JSON.nonstrict
        ) = string(HttpContentTypes.Application.Json, json.stringify(strategy, value))

        fun <T> serialize(
            strategy: SerializationStrategy<T>,
            value: T,
            cbor: CBOR
        ) = byteArray(HttpContentTypes.Application.Cbor, cbor.dump(strategy, value))

        fun <T> serialize(
            strategy: SerializationStrategy<T>,
            value: T,
            protoBuf: ProtoBuf
        ) = byteArray(HttpContentTypes.Application.Protobuf, protoBuf.dump(strategy, value))
    }

    abstract val contentType: String

    data class BString(override val contentType: String, val value: String) : HttpBody()
    class BByteArray(override val contentType: String, val value: ByteArray) : HttpBody()
    class BInput(override val contentType: String, val value: ByteReadPacket) : HttpBody()
}