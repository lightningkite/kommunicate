package com.lightningkite.kommunicate

sealed class HttpBody {
    companion object {
        val EMPTY = BString("", "")
        fun string(contentType: String, value: String) = BString(contentType, value)
        fun byteArray(contentType: String, value: ByteArray) = BByteArray(contentType, value)
    }

    abstract val contentType: String

    data class BString(override val contentType: String, val value: String) : HttpBody()
    class BByteArray(override val contentType: String, val value: ByteArray) : HttpBody()
}
