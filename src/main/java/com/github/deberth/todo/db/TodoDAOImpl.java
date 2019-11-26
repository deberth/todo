package com.github.deberth.todo.db;

import com.github.deberth.todo.api.Todo;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TodoDAOImpl extends AbstractDAO<Todo> implements TodoDAO {

    public TodoDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Todo> findAll() {
        return list(namedQuery("com.github.deberth.todo.api.Todo.findAll"));
    }

    public Todo find(int id) {
        return get(id);
    }

    public Todo create(Todo todo) {
        return persist(todo);
    }

    public void update(int id, Todo todo) {
            currentSession().merge(todo);
    }

    public void remove(int id) {
            Todo todo = get(id);
            if (todo != null) {
                currentSession().remove(todo);
            }
    }

}