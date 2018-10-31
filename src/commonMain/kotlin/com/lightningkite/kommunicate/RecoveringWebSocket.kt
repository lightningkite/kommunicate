package com.lightningkite.kommunicate

class RecoveringWebSocket<OUTBOUND, INBOUND>(val generator: () -> HttpWebSocket<OUTBOUND, INBOUND>?) :
    HttpWebSocket<OUTBOUND, INBOUND> {

    var underlying: HttpWebSocket<OUTBOUND, INBOUND>? = null
        set(value) {
            field = value
            reset()
        }
    var closed = false
    var connected = false

    override var onDisconnect: (closureCode: Int?, closureReason: String?, closureThrowable: Throwable?) -> Unit =
        { _, _, _ -> }

    override var onMessage: (INBOUND) -> Unit = {}

    override fun send(data: OUTBOUND) = underlying?.send(data) ?: Unit

    fun reset() {
        val under = underlying
        if (under == null) {
            connected = false
            underlying = generator.invoke()
        } else {
            connected = true
            under.onMessage = { onMessage.invoke(it) }
            under.onDisconnect = { code, reason, throwable ->
                onDisconnect.invoke(code, reason, throwable)
                if (!closed) {
                    underlying = null
                }
            }
        }
    }

    override fun close() {
        closed = true
        underlying?.close()
    }

    override fun close(code: Int, reason: String) {
        closed = true
        underlying?.close(code, reason)
    }

    init {
        reset()
    }
}
