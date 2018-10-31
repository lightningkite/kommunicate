package com.lightningkite.kommunicate

import kotlinx.serialization.SerialFormat
import kotlinx.serialization.cbor.CBOR
import kotlinx.serialization.json.JSON
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.reflect.KClass

private val SerialFormatContentType = HashMap<KClass<out SerialFormat>, String>().apply {
    this[JSON::class] = HttpContentTypes.Application.Json
    this[ProtoBuf::class] = HttpContentTypes.Application.Protobuf
    this[CBOR::class] = HttpContentTypes.Application.Cbor
}
val SerialFormat.contentType: String? get() = this::class.contentType
var KClass<out SerialFormat>.contentType: String?
    get() = SerialFormatContentType[this]
    set(value) {
        if (value != null) {
            SerialFormatContentType[this] = value
        } else {
            SerialFormatContentType.remove(this)
        }
    }