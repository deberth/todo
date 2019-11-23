package com.github.deberth.todo.db;

import com.github.deberth.todo.api.Task;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;

public class TaskDAOImpl extends AbstractDAO<Task> implements TaskDAO {

        public TaskDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Task> findAll() {
        return list(namedQuery("com.github.deberth.todo.api.Task.findAll"));
    }

    public Task find(int id) {
        return get(id);
    }

    public Task create(Task Task) {
        return persist(Task);
    }

    public void update(int id, Task Task) {
        persist(Task);
    }

    public void remove(int id) {
            Task Task = get(id);
            if (Task != null) {
                currentSession().remove(Task);
            }
    }

}