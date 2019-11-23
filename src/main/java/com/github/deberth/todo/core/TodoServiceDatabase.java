package com.github.deberth.todo.core;

import com.github.deberth.todo.api.Todo;
import com.github.deberth.todo.db.TaskDAO;
import com.github.deberth.todo.db.TodoDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

public class TodoServiceDatabase extends TodoService{

    public TodoServiceDatabase(TodoDAO todoDAO, TaskDAO taskDAO) {
        super(todoDAO, taskDAO);
    }

    @Override
    public List<Todo> findAllTodos() {
        return this.todoDAO.findAll();
    }

    @Override
    public Todo findTodoById(Integer id) {
        return this.todoDAO.find(id);
    }

    @Override
    public Todo createNewTodo(@NotNull @Valid Todo todo) {
        todo.setTasks(todo.getTasks().stream().map(t -> this.taskDAO.create(t)).collect(Collectors.toList()));
        return this.todoDAO.create(todo);
    }

    @Override
    public void removeTodo(Integer id) {
        Todo todo = this.todoDAO.find(id);
        todo.getTasks().forEach(t -> this.taskDAO.remove(t.getId()));
        this.todoDAO.remove(id);
    }

    @Override
    public void updateTodo(@NotNull @Valid Todo todo) {
        Todo found = this.todoDAO.find(todo.getId());
        todo.getTasks().forEach(t -> this.taskDAO.remove(t.getId()));
        this.todoDAO.update(todo.getId(), todo);
    }


}
