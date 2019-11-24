package com.github.deberth.todo.core;

import com.github.deberth.todo.db.*;
import org.hibernate.SessionFactory;

public class TodoServiceFactory {

    public static TodoService getTodoService(String type, Object... params) {
        if (type.equalsIgnoreCase("local")) {
            return new TodoServiceLocal(new TodoDAOImplLocal(), new TaskDAOImplLocal());
        } else if (type.equalsIgnoreCase("database")) {
            return new TodoServiceDatabase(new TodoDAOImpl((SessionFactory) params[0]), new TaskDAOImpl((SessionFactory) params[0]));
        }

        return null;
    }

}
