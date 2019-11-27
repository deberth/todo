package com.github.deberth.todo.core;

import com.github.deberth.todo.api.Todo;
import com.github.deberth.todo.db.TaskDAO;
import com.github.deberth.todo.db.TodoDAO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class TodoServiceDatabase extends TodoService{

    public TodoServiceDatabase(TodoDAO todoDAO, TaskDAO taskDAO) {
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
            return new TodoServiceResponse(NOT_FOUND, null);
        }

        return new TodoServiceResponse(OK, found);
    }

    @Override
    public TodoServiceResponse createNewTodo(@NotNull @Valid Todo todo) {

        // Avoid invalid POST with already existing ID
        if (todo.getId() != null) {
            Todo found = this.todoDAO.find(todo.getId());
            if (found != null) {
                return new TodoServiceResponse(CONFLICT);
            }
        }

        Todo createdTodo = this.todoDAO.create(todo);

        return new TodoServiceResponse(CREATED, createdTodo);
    }

    @Override
    public TodoServiceResponse removeTodo(Integer id) {
        Todo todo = this.todoDAO.find(id);
        if (todo != null) {
            this.todoDAO.remove(id);
            return new TodoServiceResponse(NO_CONTENT, null);
        } else {
            return new TodoServiceResponse(NOT_FOUND, null);
        }
    }

    @Override
    public TodoServiceResponse updateTodo(Integer id, @NotNull @Valid Todo updatedTodo) {
        // Check for invalid differences between path id and json body id
        if (updatedTodo.getId() != null && updatedTodo.getId() != id) {
            return new TodoServiceResponse(BAD_REQUEST, null);
        }
        Todo foundTodo = this.todoDAO.find(id);
        if(foundTodo == null) {
            return createNewTodo(updatedTodo);
        }

        // Update found todo with updated information
        foundTodo.setName(updatedTodo.getName());
        foundTodo.setDescription(updatedTodo.getDescription());
        // Avoid detached tasks exception
        foundTodo.getTasks().clear();
        foundTodo.getTasks().addAll(updatedTodo.getTasks());

        this.todoDAO.update(foundTodo.getId(), foundTodo);
        return new TodoServiceResponse(NO_CONTENT, null);
    }


}
