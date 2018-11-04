# Kommunicate

By [Lightning Kite](https://lightningkite.com)

A library combining [coroutines](https://github.com/Kotlin/kotlinx.coroutines) and [io](https://github.com/Kotlin/kotlinx-io) so that you can make HTTP requests and use WebSockets!

## Discovery Guide

You can do everything through the object `HttpClient`.

All of those functions can throw:

- `ConnectionException` when there's an issue with reaching the host
- `HttpException` if the resulting response is NOT a 2xx response code

Some examples:

```kotlin
//Let's grab a list of posts
val posts: String = HttpClient.callString(
    url = "https://jsonplaceholder.typicode.com/posts",
    method = HttpMethod.GET
)

//Let's do a post!
HttpClient.callString(
    url = "https://jsonplaceholder.typicode.com/posts",
    method = HttpMethod.POST,
    body = HttpBody.string(
        contentType = "application/json",
        value = """{ "id":0, "userId":2, "title": "Test Title", "body": "Post's body" }"""
    )
)

```

## KotlinX Serialization

[Use this Gist](https://gist.github.com/UnknownJoe796/fca3322d1c568cc84f48359b5ab7a3be) to get extensions for using [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) with Kommunicate.

Be warned that kotlinx.serialization currently has various issues with native and javascript.