package services

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import models.TodoModel
import java.util.*

class TodoHttpService(config: Map<String, String>)
{
    private val baseUrl = config["url"]!! + "/todos"
    private val configUser = config["user"]!!
    private val configPassword = config["password"]!!

    private fun baseAuthenticationRequest(user: String, password: String): RequestSpecification {
//        should use auth().basic but it doesn't work proper'
//        return given().auth().basic(user, password)
        val auth = Base64.getEncoder().encode(("$user:$password").toByteArray()).toString(Charsets.UTF_8)
        return given().header("Authorization", "Basic $auth")
    }

    fun getAll(params: Map<String, Any> = emptyMap()): List<TodoModel> {
        return given()
            .params(params)
            .`when`().get(baseUrl).`as`(Array<TodoModel>::class.java).toList()
    }

    fun post(model: TodoModel): Response {
        return given()
            .contentType(ContentType.JSON)
            .body(model)
            .post(baseUrl)
    }

    fun put(id: Int, model: TodoModel): Response {
        return given()
            .contentType(ContentType.JSON)
            .body(model)
            .`when`().put("$baseUrl/$id")
    }

    fun delete(id: Int, user: String = configUser, password: String = configPassword): Response {
        return baseAuthenticationRequest(user, password)
            .`when`().delete("$baseUrl/$id")
    }

    fun deleteAll() {
        getAll().forEach {
            delete(it.id)
        }
    }
}