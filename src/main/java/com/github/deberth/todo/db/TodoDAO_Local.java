package com.github.deberth.todo.db;

import com.github.deberth.todo.api.Todo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TodoDAO_Local implements TodoDAO{

    private HashMap<Integer, Todo> TodoDB = new HashMap<>();

    public List<Todo> findAll() {
        return new ArrayList<Todo>(this.TodoDB.values());
    }

    public Todo find(int id) {
        return this.TodoDB.get(id);
    }

    public Todo create(Todo todo) {
        // TODO: hashCode ersetzen durch unique ID, welche noch nicht vorhanden ist
        return this.TodoDB.put(todo.hashCode(), todo);
    }

    public void update(int id, Todo Todo) {
        this.TodoDB.put(id, Todo);
    }

    public void remove(int id) {
        this.TodoDB.remove(id);
    }
}
