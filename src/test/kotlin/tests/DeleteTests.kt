package tests

import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class DeleteTests : CommonTest() {
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
    fun testSimpleDelete() {
        val todo = createRandomTodosAndPost().first()
        validate204ResponseCode(todoHttpService.delete(todo.id))
        validateListSize(todoHttpService.getAll(), 0)
    }

    @Test
    fun testDeleteUnExist() {
        val todo = createRandomTodosAndPost().first()
        validate404ResponseCode(todoHttpService.delete(Int.MAX_VALUE))
        validateListEqualsWithoutOrder(todoHttpService.getAll(), listOf(todo))
    }

    @Test
    fun testDeleteWithIncorrectUsername() {
        val todo = createRandomTodosAndPost().first()
        validate401ResponseCode(todoHttpService.delete(todo.id, "NotUser"))
        validateListEqualsWithoutOrder(todoHttpService.getAll(), listOf(todo))
    }

    @Test
    fun testDeleteWithIncorrectPassword() {
        val todo = createRandomTodosAndPost().first()
        validate401ResponseCode(todoHttpService.delete(todo.id, password = "NotPassword"))
        validateListEqualsWithoutOrder(todoHttpService.getAll(), listOf(todo))
    }

    @Test
    fun testDeleteWithIncorrectUserAndPassword() {
        val todo = createRandomTodosAndPost().first()
        validate401ResponseCode(todoHttpService.delete(todo.id,user = "NotUser", password = "NotPassword"))
        validateListEqualsWithoutOrder(todoHttpService.getAll(), listOf(todo))
    }
}
