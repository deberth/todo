package com.github.deberth.todo.db;

import com.github.deberth.todo.api.Todo;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TodoDAOImpl extends AbstractDAO<Todo> implements TodoDAO {

    private final org.slf4j.Logger Logger = LoggerFactory.getLogger(TodoDAOImpl.class);

    public TodoDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Todo> findAll() {
        Logger.debug("Find all todos");
        return list(namedQuery("com.github.deberth.todo.api.Todo.findAll"));
    }

    public Todo find(int id) {
        Logger.debug("Find todo with id {}", id);
        return get(id);
    }

    public Todo create(Todo todo) {
        Logger.debug("Create todo with name {}", todo.getName());
        return persist(todo);
    }

    public void update(int id, Todo todo) {
        Logger.debug("Update todo with id {}", id);
        currentSession().merge(todo);
    }

    public void remove(int id) {
        Logger.debug("Remove todo with id {}", id);
        Todo todo = get(id);
        if (todo != null) {
            Logger.info("Todo with id {} found. Removing todo", id);
            currentSession().remove(todo);
        } else {
            Logger.error("Todo with id {} NOT found. Can't remove todo", id);
        }
    }

}