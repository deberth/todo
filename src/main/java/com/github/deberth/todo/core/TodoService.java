package com.github.deberth.todo.core;

import com.github.deberth.todo.api.Todo;
import com.github.deberth.todo.db.TaskDAO;
import com.github.deberth.todo.db.TodoDAO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public abstract class TodoService {

    protected TodoDAO todoDAO;
    protected TaskDAO taskDAO;

    public TodoService(TodoDAO todoDAO, TaskDAO taskDAO) {
            this.todoDAO = todoDAO;
            this.taskDAO = taskDAO;
    }

    abstract public  List<Todo> findAllTodos();

    abstract public Todo findTodoById(Integer id);

    abstract public Todo createNewTodo(@NotNull @Valid Todo todo);

    abstract public void removeTodo(Integer id);

    abstract public void updateTodo(@NotNull @Valid Todo todo);


}
