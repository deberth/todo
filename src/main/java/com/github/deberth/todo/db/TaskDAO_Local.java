package com.github.deberth.todo.db;

import com.github.deberth.todo.api.Task;
import com.github.deberth.todo.api.Todo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class TaskDAO_Local implements DAO{

    private HashMap<Integer, Task> TaskDB = new HashMap<>();

    public List<Task> findAll() {
        return new ArrayList<Task>(this.TaskDB.values());
    }

    public Task find(int id) {
        return this.TaskDB.get(id);
    }

    public Task create(Task task) {
        return this.TaskDB.put(task.hashCode(), task);
    }

    public void update(int id, Task task) {
        this.TaskDB.put(id, task);
    }

    public void remove(int id) {
        this.TaskDB.remove(id);
    }
}
