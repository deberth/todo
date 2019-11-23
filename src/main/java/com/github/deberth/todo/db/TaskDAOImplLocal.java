package com.github.deberth.todo.db;

import com.github.deberth.todo.api.Task;
import com.github.deberth.todo.api.Todo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskDAOImplLocal implements TaskDAO{

    private static final AtomicInteger counter = new AtomicInteger();

    public static int incrementId() {
        return counter.getAndIncrement();
    }

    private HashMap<Integer, Task> TaskDB = new HashMap<>();

    public List<Task> findAll() {
        return new ArrayList<Task>(this.TaskDB.values());
    }

    public Task find(int id) {
        return this.TaskDB.get(id);
    }

    public Task create(Task task) {
        // TODO: hashCode ersetzen durch unique ID, welche noch nicht vorhanden ist
        task.setId(incrementId());
        this.TaskDB.put(task.getId(), task);

        return task;
    }

    public void update(int id, Task task) {
        this.TaskDB.put(id, task);
    }

    public void remove(int id) {
        this.TaskDB.remove(id);
    }
}
