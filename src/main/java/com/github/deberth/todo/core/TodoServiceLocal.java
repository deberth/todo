package com.github.deberth.todo.core;

import com.github.deberth.todo.api.Todo;
import com.github.deberth.todo.db.TaskDAO;
import com.github.deberth.todo.db.TodoDAO;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.stream.Collectors;

public class TodoServiceLocal extends TodoService{

    private final org.slf4j.Logger Logger = LoggerFactory.getLogger(TodoServiceDatabase.class);

    TodoServiceLocal(TodoDAO todoDAO, TaskDAO taskDAO) {
        super(todoDAO, taskDAO);
    }

    @Override
    public TodoServiceResponse findAllTodos() {
        Logger.debug("FindAllTodos");
        return new TodoServiceResponse(OK, this.todoDAO.findAll());
    }

    @Override
    public TodoServiceResponse findTodoById(Integer id) {

        Logger.debug("FindTodoById with id {}", id);
        Todo found = this.todoDAO.find(id);
        if (found == null) {
            Logger.info("Todo with id {} not found", id);
            return new TodoServiceResponse(NOT_FOUND, null);
        }

        Logger.info("Todo with id {} has been found", id);

        return new TodoServiceResponse(OK, found);
    }

    @Override
    public TodoServiceResponse createNewTodo(@NotNull @Valid Todo todo) {

        Logger.debug("CreateNewTodo with name {} and description {}", todo.getName(), todo.getDescription());
        todo.setTasks(todo.getTasks().stream().map(t -> this.taskDAO.create(t)).collect(Collectors.toSet()));

        return new TodoServiceResponse(CREATED, this.todoDAO.create(todo));
    }

    @Override
    public TodoServiceResponse removeTodo(Integer id) {

        Logger.debug("RemoveTodo with id {}", id);
        Todo todo = this.todoDAO.find(id);
        if (todo != null) {
            Logger.debug("Removing tasks for todo with id {}", id);
            todo.getTasks().forEach(t -> this.taskDAO.remove(t.getId()));
            Logger.debug("Trying to remove todo with id {}", id);
            this.todoDAO.remove(id);
            Logger.info("Removed todo with id {}", id);
            return new TodoServiceResponse(NO_CONTENT, null);
        } else {
            Logger.info("Could not find todo with id {}", id);
            return new TodoServiceResponse(NOT_FOUND, null);
        }
    }

    @Override
    public TodoServiceResponse updateTodo(Integer id, @NotNull @Valid Todo todo) {

        Logger.debug("UpdateTodo with id {}", id);
        Todo existing = this.todoDAO.find(id);
        if (existing != null) {
            // Remove existing task collection
            existing.getTasks().forEach(t -> {
                this.taskDAO.remove(t.getId());
            });
            // Write new task collection
            todo.getTasks().forEach(t -> {
                this.taskDAO.create(t);
            });
            Logger.debug("Attempting update in local map");
            this.todoDAO.update(todo.getId(), todo);
            Logger.info("Todo with id {} updated", id);

            return new TodoServiceResponse(NO_CONTENT, null);
        } else {
            Logger.info("No todo found with id {}. Creating a new one", id);
            return createNewTodo(todo);
        }



    }
}
