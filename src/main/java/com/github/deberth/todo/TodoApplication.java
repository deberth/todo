package com.github.deberth.todo;

import com.github.deberth.todo.api.Task;
import com.github.deberth.todo.api.Todo;
import com.github.deberth.todo.core.TodoServiceFactory;
import com.github.deberth.todo.core.auth.TodoAuthenticator;
import com.github.deberth.todo.core.auth.User;
import com.github.deberth.todo.health.AppCheck;
import com.github.deberth.todo.resources.TodoResource;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.dhatim.dropwizard.correlationid.CorrelationIdBundle;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;


public class TodoApplication extends Application<TodoConfiguration> {

    private final Logger Logger = LoggerFactory.getLogger(TodoApplication.class);

    private final HibernateBundle<TodoConfiguration> hibernate = new HibernateBundle<TodoConfiguration>(Todo.class, Task.class) {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(TodoConfiguration todoConfiguration) {
            return todoConfiguration.getSourceFactory();
        }
    };

    private final SwaggerBundle swagger = new SwaggerBundle<TodoConfiguration>() {
        @Override
        protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(TodoConfiguration todoConfiguration) {
            return todoConfiguration.getSwaggerConfiguration();
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
        bootstrap.addBundle(CorrelationIdBundle.getDefault());
        bootstrap.addBundle(swagger);
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
    }

    @Override
    public void run(final TodoConfiguration configuration,
                    final Environment environment) {
        Logger.info("Starting application");
        String storageType = configuration.getStorage();
        Logger.info("Storage type: {}", storageType);
        environment.jersey().register(
                new TodoResource(TodoServiceFactory.getTodoService(storageType, hibernate.getSessionFactory()))
        );

        Logger.info("Flyway migration - Start");
        // Database migration
        Flyway flyway = new Flyway();
        DataSourceFactory f = configuration.getSourceFactory();
        flyway.setDataSource(f.getUrl(), f.getUser(), f.getPassword());
        flyway.migrate();
        Logger.info("Flyway migration - Done");

        //Application health check
        final Client client = new JerseyClientBuilder(environment).build("healthClient");
        environment.healthChecks().register("appHealth", new AppCheck(client));

        // Auth feature
        environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
        .setAuthenticator(new TodoAuthenticator())
        .setRealm("BASIC-AUTH-REALM")
        .buildAuthFilter()));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));

        Logger.info("Setup Done - Ready for queries");
    }



}
