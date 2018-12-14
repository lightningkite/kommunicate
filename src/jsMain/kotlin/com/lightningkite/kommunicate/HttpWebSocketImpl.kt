package com.lightningkite.kommunicate

import org.w3c.dom.ARRAYBUFFER
import org.w3c.dom.BinaryType
import org.w3c.dom.CloseEvent
import org.w3c.dom.WebSocket

class HttpWebSocketImpl<T>(
    url: String,
    val write: WebSocket.(T) -> Unit,
    val read: (Any?) -> T,
    onReady: (Result<HttpWebSocketImpl<T>>) -> Unit
) : HttpWebSocket<T, T> {
    val onReady = run {
        var already = false
        label@{ it: Result<HttpWebSocketImpl<T>> ->
            if (already) return@label
            already = true
            onReady.invoke(it)
        }
    }
    val underlying = WebSocket(url)

    override var onMessage: (T) -> Unit = {}
    override var onDisconnect: (closureCode: Int?, closureReason: String?, closureThrowable: Throwable?) -> Unit =
        { _, _, _ -> }

    override fun send(data: T) {
        write.invoke(underlying, data)
    }

    init {
        underlying.binaryType = BinaryType.ARRAYBUFFER
        underlying.onopen = {
            onReady.invoke(Result.success(this))
            Unit
        }
        underlying.onmessage = {
            @Suppress("USELESS_CAST")
            val received = it.asDynamic().data as Any?
            onMessage.invoke(read.invoke(received))
            Unit
        }
        underlying.onerror = {
            onReady.invoke(Result.failure(Exception()))
        }
        underlying.onclose = {
            val event = it as CloseEvent

            onDisconnect.invoke(
                event.code.toInt(),
                event.reason,
                null
            )
            Unit
        }
    }

    override fun close() = close(1000, "Normal closure")
    override fun close(code: Int, reason: String) {
        underlying.close(code.toShort(), reason)
    }
}
