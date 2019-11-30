package com.github.deberth.todo.db;

import com.github.deberth.todo.api.Todo;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TodoDAOImplLocal implements TodoDAO {

    private final org.slf4j.Logger Logger = LoggerFactory.getLogger(TodoDAOImplLocal.class);
    private static final AtomicInteger counter = new AtomicInteger();


    public static int incrementId() {
        return counter.getAndIncrement();
    }

    private final HashMap<Integer, Todo> TodoDB = new HashMap<>();

    public List<Todo> findAll() {
        Logger.debug("Find all todos");
        return new ArrayList<Todo>(this.TodoDB.values());
    }

    public Todo find(int id) {
        Logger.debug("Find todo with id {}", id);
        return this.TodoDB.get(id);
    }

    public Todo create(Todo todo) {
        Logger.debug("Create todo with name {}", todo.getName());
        todo.setId(incrementId());
        this.TodoDB.put(todo.getId(), todo);

        return todo;
    }

    public void update(int id, Todo Todo) {
        Logger.debug("Update todo with id {}", id);
        this.TodoDB.put(id, Todo);
    }

    public void remove(int id) {
        Logger.debug("Remove todo with id {}", id);
        this.TodoDB.remove(id);
    }

}
