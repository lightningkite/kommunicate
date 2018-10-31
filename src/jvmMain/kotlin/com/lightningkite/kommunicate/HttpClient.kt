package com.lightningkite.kommunicate

import kotlinx.io.core.ByteReadPacket
import kotlinx.io.core.readBytes
import kotlinx.io.streams.readPacketAtLeast
import okhttp3.*
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

actual object HttpClient {

    var resultThread: (() -> Unit) -> Unit = { it.invoke() }
    val okClient = OkHttpClient()

    fun HttpBody.toOk(method: HttpMethod): RequestBody? {
        if (method == HttpMethod.GET) return null
        return when (this) {
            is HttpBody.BByteArray -> {
                RequestBody.create(MediaType.parse(contentType), this.value)
            }
            is HttpBody.BString -> {
                RequestBody.create(MediaType.parse(contentType), this.value)
            }
            is HttpBody.BInput -> {
                RequestBody.create(MediaType.parse(contentType), this.value.readBytes())
            }
        }
    }

    fun <T> Response.toKotlin(read: Response.() -> T): HttpResponse<T> {
        return if (code() / 100 == 2) {
            HttpResponse(
                code = code(),
                headers = headers().toMultimap(),
                result = read()
            )
        } else {
            val code = code()
            HttpResponse(
                code = code,
                headers = headers().toMultimap(),
                failure = HttpException(code, body()?.string() ?: "")
            )
        }
    }

    actual suspend fun callStringDetail(
        url: String,
        method: HttpMethod,
        body: HttpBody,
        headers: Map<String, List<String>>
    ): HttpResponse<String> = suspendCoroutine { callback ->
        val rq = Request.Builder()
            .url(url)
            .method(method.name, body.toOk(method))
            .let {
                for (header in headers) {
                    for (entry in header.value) {
                        it.header(header.key, entry)
                    }
                }
                it
            }
            .build()
        okClient.newCall(rq).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                resultThread.invoke {
                    callback.resumeWithException(ConnectionException(e.message ?: "", e))
                }
            }

            override fun onResponse(call: Call, response: Response) {
                resultThread.invoke {
                    callback.resume(response.toKotlin { body()!!.string() })
                }
            }

        })
    }

    actual suspend fun callByteArrayDetail(
        url: String,
        method: HttpMethod,
        body: HttpBody,
        headers: Map<String, List<String>>
    ): HttpResponse<ByteArray> = suspendCoroutine { callback ->
        val rq = Request.Builder()
            .url(url)
            .method(method.name, body.toOk(method))
            .let {
                for (header in headers) {
                    for (entry in header.value) {
                        it.header(header.key, entry)
                    }
                }
                it
            }
            .build()
        okClient.newCall(rq).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                resultThread.invoke {
                    callback.resumeWithException(ConnectionException(e.message ?: "", e))
                }
            }

            override fun onResponse(call: Call, response: Response) {
                resultThread.invoke {
                    callback.resume(response.toKotlin { body()!!.bytes() })
                }
            }

        })
    }

    actual suspend fun callOutputDetail(
        url: String,
        method: HttpMethod,
        body: HttpBody,
        headers: Map<String, List<String>>
    ): HttpResponse<ByteReadPacket> = suspendCoroutine { callback ->
        val rq = Request.Builder()
            .url(url)
            .method(method.name, body.toOk(method))
            .let {
                for (header in headers) {
                    for (entry in header.value) {
                        it.header(header.key, entry)
                    }
                }
                it
            }
            .build()
        okClient.newCall(rq).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                resultThread.invoke {
                    callback.resumeWithException(ConnectionException(e.message ?: "", e))
                }
            }

            override fun onResponse(call: Call, response: Response) {
                resultThread.invoke {
                    callback.resume(response.toKotlin {
                        body()!!.byteStream().readPacketAtLeast(Int.MAX_VALUE.toLong())
                    })
                }
            }

        })
    }

    actual suspend fun socketString(url: String): HttpWebSocket<String, String> = suspendCoroutine { callback ->
        try {
            val handler = HttpWebSocketStringHandler()
            val underlying = HttpClient.okClient.newWebSocket(
                Request.Builder().url(url).build(),
                handler
            )
            handler.init(underlying) {
                callback.resumeWith(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callback.resumeWithException(ConnectionException(e.message ?: "", e))
        }
    }

    actual suspend fun socketByteArray(url: String): HttpWebSocket<ByteArray, ByteArray> =
        suspendCoroutine { callback ->
            try {
                val handler = HttpWebSocketByteArrayHandler()
                val underlying = HttpClient.okClient.newWebSocket(
                    Request.Builder().url(url).build(),
                    handler
                )
                handler.init(underlying) {
                    callback.resumeWith(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback.resumeWithException(ConnectionException(e.message ?: "", e))
            }
        }
}
