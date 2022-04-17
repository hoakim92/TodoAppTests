package tests

import com.fasterxml.jackson.databind.ObjectMapper
import context.TestContext
import io.restassured.RestAssured
import io.restassured.filter.log.LogDetail
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.response.Response
import models.TodoModel
import org.apache.http.HttpStatus.*
import services.ConfigReaderService
import services.PerformanceMeterService
import services.TodoHttpService
import kotlin.test.assertEquals
import kotlin.test.assertTrue

open class CommonTest {
    private val config = ConfigReaderService.getConfig()
    val baseUrl = config["url"]!!
    val objMapper = ObjectMapper()
    val todoHttpService = TodoHttpService(config)
    val performanceMeterService = PerformanceMeterService(config)

    fun commonBefore() {
        todoHttpService.deleteAll()
        TestContext.cleanContext()
    }

    fun commonAfter() {
        try {
            TestContext.getAllIds().map { todo ->
                todoHttpService.delete(todo.id)
            }
        } catch (e: Exception) {
            println("Error while clean text context")
        }
    }

    private fun validateResponseCode(response: Response, code: Int) {
        assertEquals(response.statusCode, code)
    }

    fun validate201ResponseCode(response: Response) {
        validateResponseCode(response, SC_CREATED)
    }

    fun validate200ResponseCode(response: Response) {
        validateResponseCode(response, SC_OK)
    }

    fun validate204ResponseCode(response: Response) {
        validateResponseCode(response, SC_NO_CONTENT)
    }

    fun validateInvalidDataResponse(response: Response) {
        validateResponseCode(response, SC_BAD_REQUEST)
    }

    fun validate404ResponseCode(response: Response) {
        validateResponseCode(response, SC_NOT_FOUND)
    }

    fun validate401ResponseCode(response: Response) {
        validateResponseCode(response, SC_UNAUTHORIZED)
    }

    fun <T> validateListHaveOneSuchElement(list: List<T>, element: T) {
        assertTrue(list.filter { it == element }.size == 1, "$list should contain one value such $element")
    }

    fun validateThatListsSizeEquals(expected: List<Any>, actual: List<Any>) {
        assertEquals(
            expected.size,
            actual.size,
            "Lists should have equal sizes, but actual list size ${actual.size} and expected size ${expected.size}"
        )
    }

    fun validateListSize(list: List<Any>, size: Int) {
        assertEquals(
            size,
            list.size,
            "Lists should have ${size} size, but have ${list.size}"
        )
    }

    fun validateListEqualsWithoutOrder(expected: List<Any>, actual: List<Any>) {
        assertEquals(expected.size, actual.size)
        assertTrue(expected.containsAll(actual))
        assertTrue(actual.containsAll(expected))
    }

    fun createRandomTodosAndPost(count: Int = 1): List<TodoModel> {
        val todos = TestContext.createRandomTodos(count)
        todos.map { t ->
            validate201ResponseCode(todoHttpService.post(t))
        }
        return todos
    }

    fun postAndUpdateTodo(
        f: (input: TodoModel) -> TodoModel,
        initialTodo: TodoModel = createRandomTodosAndPost().first()
    ): TodoModel {
        val updatedTodo = f.invoke(initialTodo)
        TestContext.addTodo(updatedTodo)
        validate200ResponseCode(todoHttpService.put(initialTodo.id, updatedTodo))
        validateListHaveOneSuchElement(todoHttpService.getAll(), updatedTodo)
        return updatedTodo
    }

    companion object {
        fun setupRestAssuredLogs() {
            RestAssured.filters(RequestLoggingFilter(LogDetail.ALL), ResponseLoggingFilter(LogDetail.ALL))
        }
    }
}