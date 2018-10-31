package com.lightningkite.kommunicate

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list
import kotlin.test.Test

class JsonTest {

    @Serializable
    data class Post(
        var id: Long? = null,
        var userId: Long = 0,
        var title: String = "",
        var body: String = ""
    )

    @Test
    fun serializationOnly() {
        println(JSON.stringify(Post.serializer(), Post(title = "Title", body = "This is the test post.")))
    }

    @Test
    fun testGet() {
        runBlocking {
            println(HttpClient.callSerializer(
                url = "https://jsonplaceholder.typicode.com/posts",
                method = HttpMethod.GET,
                serializer = JSON,
                strategy = Post.serializer().list
            ).joinToString { it.toString() })
        }
    }

    @Test
    fun testPost() {
        runBlocking {
            println(
                HttpClient.callSerializer(
                    url = "https://jsonplaceholder.typicode.com/posts",
                    method = HttpMethod.POST,
                    body = HttpBody.serialize(
                        Post.serializer(), Post(
                            title = "Arrogant Example Post",
                            body = "Your posts should aspire to be like this one."
                        )
                    ),
                    serializer = JSON,
                    strategy = Post.serializer()
                ).toString()
            )
        }
    }
}