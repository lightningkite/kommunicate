package com.lightningkite.kommunicate

import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class JsonTest {

    data class Post(
        var id: Long? = null,
        var userId: Long = 0,
        var title: String = "",
        var body: String = ""
    )


    @Test
    fun testGet() {
        runBlocking {
            println(
                HttpClient.callString(
                url = "https://jsonplaceholder.typicode.com/posts",
                    method = HttpMethod.GET
                )
            )
        }
    }

    @Test
    fun testPost() {
        runBlocking {
            println(
                HttpClient.callString(
                    url = "https://jsonplaceholder.typicode.com/posts",
                    method = HttpMethod.POST,
                    body = HttpBody.string(
                        contentType = "application/json",
                        value = """{ "id":0, "userId":2, "title": "Test Title", "body": "Post's body" }"""
                    )
                )
            )
        }
    }
}