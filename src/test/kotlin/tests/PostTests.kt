package tests

import context.TestContext
import org.junit.Test
import models.TodoModel
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import utils.RandomUtils.Companion.getRandomPositiveInt
import utils.RandomUtils.Companion.getRandomString


class PostTests : CommonTest() {
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
    fun testSimplePost() {
        val todo = TestContext.createRandomTodo()
        validate201ResponseCode(todoHttpService.post(todo))
    }

    @Test
    fun testPostSameText() {
        val text = getRandomString(8)
        val todoFirst = TestContext.createRandomTodo(text = text)
        val todoSecond = TestContext.createRandomTodo(text = text)
        validate201ResponseCode(todoHttpService.post(todoFirst))
        validate201ResponseCode(todoHttpService.post(todoSecond))
        val todos = todoHttpService.getAll()
        validateListSize(todos, 2)
        validateListHaveOneSuchElement(todos, todoFirst)
        validateListHaveOneSuchElement(todos, todoSecond)
    }

    @Test
    fun testPostSameTextDifferentCompleted() {
        val text = getRandomString(8)
        val todoFirst = TestContext.createRandomTodo(text = text, true)
        val todoSecond = TestContext.createRandomTodo(text = text, false)
        validate201ResponseCode(todoHttpService.post(todoFirst))
        validate201ResponseCode(todoHttpService.post(todoSecond))
        val todos = todoHttpService.getAll()
        validateListSize(todos, 2)
        validateListHaveOneSuchElement(todos, todoFirst)
        validateListHaveOneSuchElement(todos, todoSecond)
        TestContext.getAllIds()
    }

    @Test
    fun testPostEmptyTextCompleted() {
        val todo = TestContext.createRandomTodo(text = "", true)
        validate201ResponseCode(todoHttpService.post(todo))
    }

    @Test
    fun testPostEmptyTextNotCompleted() {
        val todo = TestContext.createRandomTodo(text = "", false)
        validate201ResponseCode(todoHttpService.post(todo))
    }

    @Test
    fun testPostThousandSymbolsText() {
        val todo = TestContext.createRandomTodo(text = getRandomString(1000))
        validate201ResponseCode(todoHttpService.post(todo))
    }

    @Test
    fun testPostSameIdsTwice() {
        val todo = TestContext.createRandomTodo()
        validate201ResponseCode(todoHttpService.post(todo))
        validateInvalidDataResponse(todoHttpService.post(todo))
    }

    @Test
    fun testPostSameIdsDifferentTextTwice() {
        val id = getRandomPositiveInt()
        val todoFirst = TodoModel(id, getRandomString(), true)
        val todoSecond = TodoModel(id, getRandomString(), true)
        TestContext.addTodos(todoFirst, todoSecond)
        validate201ResponseCode(todoHttpService.post(todoFirst))
        validateInvalidDataResponse(todoHttpService.post(todoSecond))
        TestContext.getAllIds()
    }

//    @Test
//    fun testPostWithIncorrectUsername() {
//        val todo = TestContext.createRandomTodo()
//        validateFailResponse(todoHttpService.post(model = todo, user = "BadUser"))
//        validateListShouldntContains(todoHttpService.getAll(), todo)
//    }
//
//    @Test
//    fun testPostWithIncorrectPassword() {
//        val todo = TestContext.createRandomTodo()
//        validateFailResponse(todoHttpService.post(model = todo, password = "BadPassword"))
//        validateListShouldntContains(todoHttpService.getAll(), todo)
//    }
//
//    @Test
//    fun testPostWithIncorrectUsernameAndPassword() {
//        val todo = TestContext.createRandomTodo()
//        validateFailResponse(todoHttpService.post(model = todo, user = "BadUser", password = "BadPassword"))
//        validateListShouldntContains(todoHttpService.getAll(), todo)
//    }

    @Test
    fun postHundredTodos() {
        val generatedTodos = TestContext.createRandomTodos(100)
        generatedTodos.forEach { t ->
            validate201ResponseCode(todoHttpService.post(t))
        }
        val todos = todoHttpService.getAll()
        validateThatListsSizeEquals(generatedTodos, todos)
        generatedTodos.forEach { t ->
            validateListHaveOneSuchElement(todos, t)
        }

    }

}