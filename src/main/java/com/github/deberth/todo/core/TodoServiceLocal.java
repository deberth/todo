package com.github.deberth.todo.core;

import com.github.deberth.todo.api.Task;
import com.github.deberth.todo.api.Todo;
import com.github.deberth.todo.db.TaskDAO;
import com.github.deberth.todo.db.TodoDAO;
import com.github.deberth.todo.db.TodoDAOImplLocal;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

public class TodoServiceLocal extends TodoService{

    public TodoServiceLocal(TodoDAO todoDAO, TaskDAO taskDAO) {
        super(todoDAO, taskDAO);
    }

    @Override
    public TodoServiceResponse findAllTodos() {
        return new TodoServiceResponse(OK, this.todoDAO.findAll());
    }

    @Override
    public TodoServiceResponse findTodoById(Integer id) {
        Todo found = this.todoDAO.find(id);
        if (found == null) {
            new TodoServiceResponse(NOT_FOUND, null);
        }

        return new TodoServiceResponse(OK, found);
    }

    @Override
    public TodoServiceResponse createNewTodo(@NotNull @Valid Todo todo) {
        todo.setTasks(todo.getTasks().stream().map(t -> this.taskDAO.create(t)).collect(Collectors.toSet()));

        return new TodoServiceResponse(CREATED, this.todoDAO.create(todo));
    }

    @Override
    public TodoServiceResponse removeTodo(Integer id) {
        Todo todo = this.todoDAO.find(id);
        todo.getTasks().forEach(t -> this.taskDAO.remove(t.getId()));
        this.todoDAO.remove(id);

        return new TodoServiceResponse(NO_CONTENT, null);
    }

    @Override
    public TodoServiceResponse updateTodo(Integer id, @NotNull @Valid Todo todo) {
        Todo existing = this.todoDAO.find(id);
        // Remove existing task collection
        existing.getTasks().forEach(t -> {
            this.taskDAO.remove(t.getId());
        });
        // Write new task collection
        todo.getTasks().forEach(t -> {
            this.taskDAO.create(t);
        });

        this.todoDAO.update(todo.getId(), todo);

        return new TodoServiceResponse(NO_CONTENT, null);
    }


}
