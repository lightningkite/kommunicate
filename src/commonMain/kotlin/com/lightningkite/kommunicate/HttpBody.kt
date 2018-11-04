package com.lightningkite.kommunicate

import kotlinx.io.core.ByteReadPacket

sealed class HttpBody {
    companion object {
        val EMPTY = BString("", "")
        fun string(contentType: String, value: String) = BString(contentType, value)
        fun byteArray(contentType: String, value: ByteArray) = BByteArray(contentType, value)
        fun byteReadPacket(contentType: String, value: ByteReadPacket) = BInput(contentType, value)
    }

    abstract val contentType: String

    data class BString(override val contentType: String, val value: String) : HttpBody()
    class BByteArray(override val contentType: String, val value: ByteArray) : HttpBody()
    class BInput(override val contentType: String, val value: ByteReadPacket) : HttpBody()
}