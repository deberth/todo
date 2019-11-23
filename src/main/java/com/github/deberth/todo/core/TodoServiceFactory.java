package com.github.deberth.todo.core;

import com.github.deberth.todo.db.TaskDAO;
import com.github.deberth.todo.db.TaskDAOImplLocal;
import com.github.deberth.todo.db.TodoDAOImplLocal;

public class TodoServiceFactory {

    public static TodoService getTodoService(String type) {
        if (type.equalsIgnoreCase("local")) {
            return new TodoServiceLocal(new TodoDAOImplLocal(), new TaskDAOImplLocal());
        }

        return null;
    }

}
