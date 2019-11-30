package com.github.deberth.todo.db;

import com.github.deberth.todo.api.Task;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TaskDAOImpl extends AbstractDAO<Task> implements TaskDAO {

    private final org.slf4j.Logger Logger = LoggerFactory.getLogger(TaskDAOImpl.class);

    public TaskDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Task> findAll() {
        Logger.debug("Find all tasks");
        return list(namedQuery("com.github.deberth.todo.api.Task.findAll"));
    }

    public Task find(int id) {
        Logger.debug("Find task with id {}", id);
        return get(id);
    }

    public Task create(Task task) {
        Logger.debug("Create task with name {}", task.getName());
        return persist(task);
    }

    public void update(int id, Task task) {
        Logger.debug("Update task with id {}", id);
        currentSession().merge(task);
    }

    public void remove(int id) {
        Logger.debug("Remove task with id {}", id);
        Task task = get(id);
        if (task != null) {
            Logger.info("Task with id {} found. Removing todo", id);
            currentSession().remove(task);
        } else {
            Logger.error("Task with id {} NOT found. Can't remove task", id);
        }
    }

}