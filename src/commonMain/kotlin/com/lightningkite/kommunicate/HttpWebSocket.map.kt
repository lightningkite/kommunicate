package com.lightningkite.kommunicate


fun <SRCOUT, SRCIN, RESOUT, RESIN> HttpWebSocket<SRCOUT, SRCIN>.map(
    mapWrite: (RESOUT) -> SRCOUT,
    mapRead: (SRCIN) -> RESIN
) = HttpWebSocketMapped(this, mapWrite, mapRead)

class HttpWebSocketMapped<SRCOUT, SRCIN, RESOUT, RESIN>(
    val wraps: HttpWebSocket<SRCOUT, SRCIN>,
    val mapWrite: (RESOUT) -> SRCOUT,
    val mapRead: (SRCIN) -> RESIN
) : HttpWebSocket<RESOUT, RESIN> {
    var cached: ((RESIN) -> Unit)? = null
    override var onMessage: (RESIN) -> Unit
        get() = cached ?: {}
        set(value) {
            cached = value
            wraps.onMessage = { it: SRCIN -> value.invoke(mapRead(it)) }
        }
    override var onDisconnect: (closureCode: Int?, closureReason: String?, closureThrowable: Throwable?) -> Unit
        get() = wraps.onDisconnect
        set(value) {
            wraps.onDisconnect = value
        }

    override fun send(data: RESOUT) {
        wraps.send(mapWrite(data))
    }

    override fun close() = wraps.close()

    override fun close(code: Int, reason: String) = wraps.close(code, reason)
}