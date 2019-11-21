package com.github.deberth.todo.db;

import com.github.deberth.todo.api.Todo;

import java.util.List;

public interface TodoDAO {

    List<Todo> findAll();

    Todo find(int id);

    Todo create(Todo todo);

    void update(int id, Todo todo);

    void remove(int id);

}
