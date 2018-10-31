package com.lightningkite.kommunicate

/**
 * Used to indicate that there was an issue with the connection.
 */
class ConnectionException(message: String, cause: Throwable? = null) : Exception(message, cause)