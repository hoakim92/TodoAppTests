package tests

import context.TestContext
import models.TodoModel
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import utils.RandomUtils.Companion.getRandomPositiveInt
import utils.RandomUtils.Companion.getRandomString

class PutTests : CommonTest() {
    companion object {
        init {
        }

        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            setupRestAssuredLogs()
        }

    }

    @After
    fun after() {
        commonAfter()
    }

    @Before
    fun before() {
        commonBefore()
    }

    @Test
    fun testPutUpdateText() {
        postAndUpdateTodo({ todo ->
            TodoModel(todo.id, getRandomString(), true)
        })
    }

    @Test
    fun testPutUpdatedCompleted() {
        postAndUpdateTodo({ todo ->
            TodoModel(todo.id, todo.text, false)
        })
    }

    @Test
    fun testPutUpdatedTextAndCompleted() {
        postAndUpdateTodo({ todo ->
            TodoModel(todo.id, getRandomString(), false)
        })
    }

    @Test
    fun testPutUpdatedId() {
        postAndUpdateTodo({ todo ->
            TodoModel(getRandomPositiveInt(), todo.text, todo.completed)
        })
    }

    @Test
    fun testPutUpdatedIdAndText() {
        postAndUpdateTodo({ todo ->
            TodoModel(getRandomPositiveInt(), getRandomString(), todo.completed)
        })
    }

    @Test
    fun testPutUpdatedIdAndTextAndCompleted() {
        postAndUpdateTodo({ todo ->
            TodoModel(getRandomPositiveInt(), getRandomString(), !todo.completed)
        })
    }

    @Test
    fun testPutSameTodo() {
        postAndUpdateTodo({ todo ->
            todo
        })
    }

    @Test
    fun changeIdTwice() {
        val afterFirstUpdate = postAndUpdateTodo({ todo ->
            TodoModel(getRandomPositiveInt(), getRandomString(), !todo.completed)
        })
        postAndUpdateTodo({ todo ->
            TodoModel(getRandomPositiveInt(), getRandomString(), !todo.completed)
        }, afterFirstUpdate)
        validateListSize(todoHttpService.getAll(), 1)
    }

    @Test
    fun testPutUnexistId() {
        validate404ResponseCode(todoHttpService.put(Int.MAX_VALUE, TestContext.createRandomTodo()))
    }
}