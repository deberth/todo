package com.github.deberth.todo.db;

import com.github.deberth.todo.api.Task;

import java.util.List;

public interface TaskDAO {

    List<Task> findAll();

    Task find(int id);

    Task create(Task task);

    void update(int id, Task task);

    void remove(int id);

}
