package com.github.deberth.todo.core;

import com.github.deberth.todo.api.Todo;
import com.github.deberth.todo.db.TaskDAO;
import com.github.deberth.todo.db.TodoDAO;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class TodoServiceDatabase extends TodoService{

    private final org.slf4j.Logger Logger = LoggerFactory.getLogger(TodoServiceDatabase.class);

    TodoServiceDatabase(TodoDAO todoDAO, TaskDAO taskDAO) {
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
        // Avoid invalid POST with already existing ID
        if (todo.getId() != null) {
            Logger.info("Found id {} in todo when trying to create.Trying to find todo in database", todo.getId());
            Todo found = this.todoDAO.find(todo.getId());
            if (found != null) {
                Logger.info("Found todo in database. Conflict");
                return new TodoServiceResponse(CONFLICT);
            } else {
                Logger.info("No todo found in database. Will be created");
            }
        }

        Todo createdTodo = this.todoDAO.create(todo);
        Logger.info("Created new todo with id {}", createdTodo.getId());

        return new TodoServiceResponse(CREATED, createdTodo);
    }

    @Override
    public TodoServiceResponse removeTodo(Integer id) {

        Logger.debug("RemoveTodo with id {}", id);
        Todo todo = this.todoDAO.find(id);
        if (todo != null) {
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
    public TodoServiceResponse updateTodo(Integer id, @NotNull @Valid Todo updatedTodo) {

        Logger.debug("UpdateTodo with id {}", id);
        // Check for invalid differences between path id and json body id
        if (updatedTodo.getId() != null && updatedTodo.getId() != id) {
            Logger.error("Update was issued for different ids. Please check your todo json");
            return new TodoServiceResponse(BAD_REQUEST, null);
        }
        Todo foundTodo = this.todoDAO.find(id);
        if(foundTodo == null) {
            Logger.info("No todo found with id {}. Creating a new one", id);
            return createNewTodo(updatedTodo);
        }

        // Update found todo with updated information
        foundTodo.setName(updatedTodo.getName());
        foundTodo.setDescription(updatedTodo.getDescription());
        // Avoid detached tasks exception
        foundTodo.getTasks().clear();
        foundTodo.getTasks().addAll(updatedTodo.getTasks());

        Logger.debug("Attempting update in database");
        this.todoDAO.update(foundTodo.getId(), foundTodo);
        Logger.info("Todo with id {} updated", id);

        return new TodoServiceResponse(NO_CONTENT, null);
    }


}
