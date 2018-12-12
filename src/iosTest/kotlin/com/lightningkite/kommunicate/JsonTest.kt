package com.lightningkite.kommunicate

import kotlinx.coroutines.*
import platform.Foundation.*
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
        var done = false
        GlobalScope.launch(Dispatchers.Unconfined) {
            println(
                    HttpClient.callString(
                            url = "https://jsonplaceholder.typicode.com/posts",
                            method = HttpMethod.GET
                    )
            )
            done = true
        }
        println("Escaped the scope $done")
        while(!done){
            println("LoopStart")
            NSRunLoop.mainRunLoop.runMode(NSDefaultRunLoopMode, beforeDate = NSDate.create(timeInterval = 1.0, sinceDate = NSDate()))
            println("LoopEnd")
        }
    }

    @Test
    fun testPost() {
        var done = false
        GlobalScope.launch(Dispatchers.Unconfined) {
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
            done = true
        }
        println("Escaped the scope $done")
        while(!done){
            println("LoopStart")
            NSRunLoop.mainRunLoop.runMode(NSDefaultRunLoopMode, beforeDate = NSDate.create(timeInterval = 1.0, sinceDate = NSDate()))
            println("LoopEnd")
        }
    }
}
