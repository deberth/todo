package com.github.deberth.todo;

import com.github.deberth.todo.api.Task;
import com.github.deberth.todo.api.Todo;
import com.github.deberth.todo.core.TodoServiceDatabase;
import com.github.deberth.todo.db.TaskDAOImpl;
import com.github.deberth.todo.db.TodoDAOImpl;
import com.github.deberth.todo.resources.TodoResource;
import io.dropwizard.Application;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TodoApplication extends Application<TodoConfiguration> {

    public static final Logger LOGGER = LoggerFactory.getLogger(TodoApplication.class);
    private final HibernateBundle<TodoConfiguration> hibernate = new HibernateBundle<TodoConfiguration>(Todo.class, Task.class) {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(TodoConfiguration todoConfiguration) {
            return todoConfiguration.getSourceFactory();
        }
    };

    public static void main(final String[] args) throws Exception {
        new TodoApplication().run(args);
    }

    @Override
    public String getName() {
        return "todo";
    }

    @Override
    public void initialize(final Bootstrap<TodoConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(final TodoConfiguration configuration,
                    final Environment environment) {
        LOGGER.info("Starting application");

        environment.jersey().register(
                //new TodoResource(TodoServiceFactory.getTodoService("local"))
                new TodoResource(new TodoServiceDatabase(new TodoDAOImpl(hibernate.getSessionFactory()), new TaskDAOImpl(hibernate.getSessionFactory())))
        );
    }

}
