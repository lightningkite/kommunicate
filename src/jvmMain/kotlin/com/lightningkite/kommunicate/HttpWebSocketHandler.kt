package com.lightningkite.kommunicate

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.io.Closeable

class HttpWebSocketStringHandler internal constructor() : HttpWebSocket<String, String>, WebSocketListener(),
    Closeable {
    lateinit var underlying: WebSocket
    lateinit var onComplete: (Result<HttpWebSocketStringHandler>) -> Unit
    private var privateClosureThrowable: Throwable? = null

    fun init(underlying: WebSocket, complete: (Result<HttpWebSocketStringHandler>) -> Unit) {
        this.underlying = underlying
        var already = false
        this.onComplete = label@{
            if (already) return@label
            already = true
            complete.invoke(it)
        }
    }

    override var onMessage: (String) -> Unit = {}
    override var onDisconnect: (closureCode: Int?, closureReason: String?, closureThrowable: Throwable?) -> Unit =
        { _, _, _ -> }

    override fun send(data: String) {
        underlying.send(data)
    }

    override fun onOpen(webSocket: WebSocket?, response: Response?) {
        onComplete.invoke(Result.success(this))
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
        privateClosureThrowable = t
        onComplete.invoke(Result.failure(t ?: Exception()))
    }

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {}

    override fun onMessage(webSocket: WebSocket?, text: String) {
        HttpClient.resultThread.invoke {
            onMessage.invoke(text)
        }
    }

    override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
        onDisconnect.invoke(code, reason, privateClosureThrowable)
    }

    override fun close() = close(1000, "Normal closure")
    override fun close(code: Int, reason: String) {
        underlying.close(code, reason)
    }
}

class HttpWebSocketByteArrayHandler internal constructor() : HttpWebSocket<ByteArray, ByteArray>, WebSocketListener(),
    Closeable {
    lateinit var underlying: WebSocket
    lateinit var onComplete: (Result<HttpWebSocketByteArrayHandler>) -> Unit
    private var privateClosureThrowable: Throwable? = null

    fun init(underlying: WebSocket, complete: (Result<HttpWebSocketByteArrayHandler>) -> Unit) {
        this.underlying = underlying
        var already = false
        this.onComplete = label@{
            if (already) return@label
            already = true
            complete.invoke(it)
        }
    }

    override var onMessage: (ByteArray) -> Unit = {}
    override var onDisconnect: (closureCode: Int?, closureReason: String?, closureThrowable: Throwable?) -> Unit =
        { _, _, _ -> }

    override fun send(data: ByteArray) {
        underlying.send(ByteString.of(data, 0, data.size))
    }

    override fun onOpen(webSocket: WebSocket?, response: Response?) {
        onComplete.invoke(Result.success(this))
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
        privateClosureThrowable = t
        onComplete.invoke(Result.failure(t ?: Exception()))
    }

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {}

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString) {
        HttpClient.resultThread.invoke {
            onMessage.invoke(bytes.toByteArray())
        }
    }

    override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
        onDisconnect.invoke(code, reason, privateClosureThrowable)
    }

    override fun close() = close(1000, "Normal closure")
    override fun close(code: Int, reason: String) {
        underlying.close(code, reason)
    }
}
