package com.lightningkite.kommunicate

data class HttpResponse<T>(
    val code: Int,
    val headers: Map<String, List<String>>,
    val result: T? = null,
    val failure: Throwable? = null
) {
    companion object {
        fun <T> failure(exception: Throwable) = HttpResponse<T>(0, mapOf(), failure = (exception))
    }
}

class HttpException(val code: Int, message: String) : Exception(message)

inline fun <I, O> HttpResponse<I>.copy(convert: (I) -> O): HttpResponse<O> {
    return HttpResponse(code = code, headers = headers, result = result?.let(convert), failure = failure)
}
