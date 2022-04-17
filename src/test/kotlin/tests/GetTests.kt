package tests

import models.TodoModel
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class GetTests : CommonTest() {
    companion object {
        init {
        }

        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            setupRestAssuredLogs()
        }

    }

    @Before
    fun before() {
        commonBefore()
    }

    @After
    fun after() {
        commonAfter()
    }

    private fun testLimitOffsetEmptyResponse(params: Map<String, Int>, generatedCount:Int = 1, expectedCount: Int = 0){
        createRandomTodosAndPost(generatedCount)
        validateListSize(todoHttpService.getAll(params), expectedCount)
    }

    @Test
    fun testSimpleGet() {
        validateListEqualsWithoutOrder(createRandomTodosAndPost(7), todoHttpService.getAll())
    }

    @Test
    fun testGetWithOffset() {
        val expectedTodos = createRandomTodosAndPost(7)
        val actualTodos = todoHttpService.getAll(mapOf("offset" to 5))
        validateListSize(actualTodos, 2)
        actualTodos.forEach {
            validateListHaveOneSuchElement(expectedTodos, it)
        }
    }

    @Test
    fun testGetWithLimit() {
        val expectedTodos = createRandomTodosAndPost(7)
        val actualTodos = todoHttpService.getAll(mapOf("limit" to 5))
        validateListSize(actualTodos, 5)
        actualTodos.forEach {
            validateListHaveOneSuchElement(expectedTodos, it)
        }
    }

    @Test
    fun testGetWithZeroLimit() {
        testLimitOffsetEmptyResponse(mapOf("limit" to 0))
    }

    @Test
    fun testGetWithMaximumOffset() {
        testLimitOffsetEmptyResponse(mapOf("offset" to Int.MAX_VALUE))
    }

    @Test
    fun testGetWithOverflowOffsetAndLimit() {
        testLimitOffsetEmptyResponse(mapOf("offset" to Int.MAX_VALUE, "limit" to Int.MAX_VALUE))
    }

    @Test
    fun testGetWithOffsetAndLimit() {
        fun getAndValidate(generated: List<TodoModel>, offset: Int, limit: Int): List<TodoModel> {
            val todosFromApp = todoHttpService.getAll(mapOf("offset" to offset, "limit" to limit))
            validateListSize(todosFromApp, limit)
            generated.containsAll(todosFromApp)
            val nextPortion = generated.minus(todosFromApp)
            validateListSize(nextPortion, generated.size - limit)
            return nextPortion
        }

        var todos = createRandomTodosAndPost(100)
        for (i in 0..9) {
            todos = getAndValidate(todos, 10 * i, 10)
        }
    }
}