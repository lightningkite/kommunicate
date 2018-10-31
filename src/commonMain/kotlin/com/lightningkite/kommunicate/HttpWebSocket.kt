package com.lightningkite.kommunicate

import kotlinx.io.core.Closeable

interface HttpWebSocket<OUTBOUND, INBOUND> : Closeable {
    var onMessage: (INBOUND) -> Unit
    var onDisconnect: (
        closureCode: Int?,
        closureReason: String?,
        closureThrowable: Throwable?
    ) -> Unit

    fun send(data: OUTBOUND)
    override fun close() = close(1000, "Normal closure")
    fun close(code: Int, reason: String)
}