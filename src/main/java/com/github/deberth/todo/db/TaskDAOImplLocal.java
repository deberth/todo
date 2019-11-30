package com.github.deberth.todo.db;

import com.github.deberth.todo.api.Task;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskDAOImplLocal implements TaskDAO{

    private final org.slf4j.Logger Logger = LoggerFactory.getLogger(TaskDAOImplLocal.class);
    private static final AtomicInteger counter = new AtomicInteger();

    public static int incrementId() {
        return counter.getAndIncrement();
    }

    private HashMap<Integer, Task> TaskDB = new HashMap<>();

    public List<Task> findAll() {
        Logger.debug("Find all tasks");
        return new ArrayList<Task>(this.TaskDB.values());
    }

    public Task find(int id) {
        Logger.debug("Find task with id {}", id);
        return this.TaskDB.get(id);
    }

    public Task create(Task task) {
        Logger.debug("Create task with name {}", task.getName());
        task.setId(incrementId());
        this.TaskDB.put(task.getId(), task);

        return task;
    }

    public void update(int id, Task task) {
        Logger.debug("Update task with id {}", id);
        this.TaskDB.put(id, task);
    }

    public void remove(int id) {
        Logger.debug("Remove task with id {}", id);
        this.TaskDB.remove(id);
    }
}
