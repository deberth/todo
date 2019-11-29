package com.github.deberth.todo;

import com.github.deberth.todo.api.Task;
import com.github.deberth.todo.api.Todo;
import com.github.deberth.todo.core.TodoServiceFactory;
import com.github.deberth.todo.health.DatabaseHealthCheck;
import com.github.deberth.todo.resources.TodoResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.flyway.FlywayFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.flywaydb.core.Flyway;
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
        String storageType = "local";

        Flyway flyway = new Flyway();
        DataSourceFactory f = configuration.getSourceFactory();
        flyway.setDataSource(f.getUrl(), f.getUser(), f.getPassword());
        flyway.migrate();

        environment.healthChecks().register("database", new DatabaseHealthCheck());
        environment.jersey().register(
                //new TodoResource(TodoServiceFactory.getTodoService(storageType)));
                new TodoResource(TodoServiceFactory.getTodoService("database", hibernate.getSessionFactory()))
        );
    }



}
