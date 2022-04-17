package tests

import models.TodoModel
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import utils.RandomUtils.Companion.getRandomPositiveInt
import utils.RandomUtils.Companion.getRandomString

class FullChainTests : CommonTest() {
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


    @Test
    fun testCRUDChain() {
        val initialTodo = createRandomTodosAndPost().first()
        val updatedTodo = postAndUpdateTodo({
            TodoModel(getRandomPositiveInt(), getRandomString(), false)
        }, initialTodo)
        todoHttpService.delete(updatedTodo.id)
        validateListSize(todoHttpService.getAll(), 0)
        validate404ResponseCode(todoHttpService.put(initialTodo.id, createRandomTodosAndPost().first()))
        validate404ResponseCode(todoHttpService.put(updatedTodo.id, createRandomTodosAndPost().first()))
        validateListSize(todoHttpService.getAll(), 0)
        validate404ResponseCode(todoHttpService.delete(initialTodo.id))
        validate404ResponseCode(todoHttpService.delete(updatedTodo.id))
        validateListSize(todoHttpService.getAll(), 0)
        validate201ResponseCode(todoHttpService.post(initialTodo))
        validate201ResponseCode(todoHttpService.post(updatedTodo))
    }
}