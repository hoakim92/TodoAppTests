package context

import models.TodoModel
import utils.RandomUtils.Companion.getRandomPositiveInt
import utils.RandomUtils.Companion.getRandomString

object TestContext {
    private val insertedTodos = mutableListOf<TodoModel>()
    fun addTodo(todo: TodoModel) {
        insertedTodos.add(todo)
    }

    fun addTodos(vararg models: TodoModel) {
        insertedTodos.addAll(models.toList())
    }

    fun getAllIds() = insertedTodos

    fun cleanContext(){
        insertedTodos.clear()
    }

    fun createRandomTodo(text: String = getRandomString(8), completed: Boolean = true): TodoModel {
        val todo = TodoModel(getRandomPositiveInt(), text, completed)
        addTodo(todo)
        return todo
    }

    fun createRandomTodos(count: Int): List<TodoModel>{
        val todos = List(count) {
            TodoModel(getRandomPositiveInt(), getRandomString(8), true)
        }
        insertedTodos.addAll(todos)
        return todos
    }
 }