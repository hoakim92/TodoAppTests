package tests

import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.junit.After
import org.junit.Test


class PerformanceTests : CommonTest() {

    @After
    fun after() {
        todoHttpService.deleteAll()
    }

    @Test
    fun postPerformanceTest() {
        performanceMeterService.exexecutePerformanceMeter {
            val post = HttpPost("$baseUrl/todos")
            post.entity = StringEntity(objMapper.writeValueAsString(it))
            post.addHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
            post
        }
    }

}