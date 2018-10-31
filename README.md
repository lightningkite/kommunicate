# Kommunicate

By [Lightning Kite](https://lightningkite.com)

A library combining [coroutines](https://github.com/Kotlin/kotlinx.coroutines), [serialization](https://github.com/Kotlin/kotlinx.serialization), and [io](https://github.com/Kotlin/kotlinx-io) so that you can make HTTP requests and use WebSockets!

## Discovery Guide

You can do everything through the object `HttpClient`.

All of those functions can throw:

- `ConnectionException` when there's an issue with reaching the host
- `HttpException` if the resulting response is NOT a 2xx response code

Some examples:

```kotlin
//Let's grab a list of posts
val posts: List<Post> = HttpClient.callSerializer(
    url = "https://jsonplaceholder.typicode.com/posts",
    method = HttpMethod.GET,
    serializer = JSON,
    strategy = Post.serializer().list
)

//Let's do a post!
HttpClient.callSerializer(
    url = "https://jsonplaceholder.typicode.com/posts",
    method = HttpMethod.POST,
    body = HttpBody.serialize(Post.serializer(), Post(
        title = "Arrogant Example Post",
        body = "Your posts should aspire to be like this one."
    )),
    serializer = JSON,
    strategy = Post.serializer()
)

```