package com.github.deberth.todo.core;

import com.github.deberth.todo.api.Todo;
import com.github.deberth.todo.db.TaskDAO;
import com.github.deberth.todo.db.TodoDAO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public abstract class TodoService {

    public static final int CREATED = 201;
    public static final int OK = 200;
    public static final int NO_CONTENT = 204;
    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;
    public static final int SERVER_ERROR = 500;
    public static final int CONFLICT = 409;

    protected TodoDAO todoDAO;
    protected TaskDAO taskDAO;

    public TodoService(TodoDAO todoDAO, TaskDAO taskDAO) {
            this.todoDAO = todoDAO;
            this.taskDAO = taskDAO;
    }

    abstract public TodoServiceResponse findAllTodos();

    abstract public TodoServiceResponse findTodoById(Integer id);

    abstract public TodoServiceResponse createNewTodo(@NotNull @Valid Todo todo);

    abstract public TodoServiceResponse removeTodo(Integer id);

    abstract public TodoServiceResponse updateTodo(Integer id, @NotNull @Valid Todo todo);


}
