package com.lightningkite.kommunicate

interface HttpWebSocket<OUTBOUND, INBOUND> {
    var onMessage: (INBOUND) -> Unit
    var onDisconnect: (
        closureCode: Int?,
        closureReason: String?,
        closureThrowable: Throwable?
    ) -> Unit

    fun send(data: OUTBOUND)
    fun close() = close(1000, "Normal closure")
    fun close(code: Int, reason: String)
}
