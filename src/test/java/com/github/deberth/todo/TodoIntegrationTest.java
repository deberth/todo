package com.github.deberth.todo;

import com.github.deberth.todo.api.Task;
import com.github.deberth.todo.api.Todo;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@ExtendWith(DropwizardExtensionsSupport.class)
class TodoIntegrationTest {

    private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("config.yml");
    @Container
    private static final PostgreSQLContainer postgres = new PostgreSQLContainer()
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("secret");
    static {
        postgres.start();
    }
    private Integer firstID;
    private Integer secondID;
    private Todo created;

    private static final String HTTP_LOCALHOST = "http://localhost:";

    private static final DropwizardAppExtension<TodoConfiguration> APP =
            new DropwizardAppExtension<TodoConfiguration>(
                    TodoApplication.class,
                    CONFIG_PATH,
                    ConfigOverride.config("database.url", postgres.getJdbcUrl()
                    ),
                    ConfigOverride.config("database.driverClass", "org.postgresql.Driver"
                    ));

    @Test
    void happyFlow() {
        CreateTodos();
        CheckTodosForSize(2);
        UpdateTodo();
        DeleteTodo();
        CheckTodosForSize(1);
    }

    private void DeleteTodo() {
        Response resp = executeRequest("/todos/" + secondID, HttpMethod.DELETE, null);
        assertThat(resp.getStatus()).isEqualTo(HttpStatus.NO_CONTENT_204);
    }

    private void UpdateTodo() {

        created.getTasks().remove(created.getTasks().iterator().next());
        created.getTasks().stream().peek(t -> t.setName("Changed"));

        Response resp = executeRequest("/todos/" + firstID, HttpMethod.PUT, Entity.json(created));
        assertThat(resp.getStatus()).isEqualTo(HttpStatus.NO_CONTENT_204);

        resp = executeRequest("/todos/" + firstID, HttpMethod.GET, null);
        assertThat(resp.getStatus()).isEqualTo(HttpStatus.OK_200);
        Todo updatedTodo = null;
        try {
            updatedTodo = resp.readEntity(Todo.class);
        } catch (Exception e) {
            Assertions.fail();
        }
        assertThat(updatedTodo.getId()).isEqualTo(firstID);
        assertThat(updatedTodo.getTasks().size()).isEqualTo(1);
        // TODO rework assertion in stream
        updatedTodo.getTasks().forEach(task -> assertThat(task.getName().equals("Changed")));
    }

    private void CheckTodosForSize(int size) {

        Response resp = executeRequest("/todos", HttpMethod.GET, null);
        assertThat(resp.getStatus()).isEqualTo(HttpStatus.OK_200);
        List foundTodos = null;
        try {
            foundTodos = resp.readEntity(List.class);
        } catch (Exception e) {
            Assertions.fail();
        }
        assertThat(foundTodos.size()).isEqualTo(size);
    }

    private void CreateTodos() {

        Set<Task> tasks = new java.util.HashSet<>();
        tasks.add(new Task("t_name1", "t_desc1"));
        tasks.add(new Task("t_name2", "t_desc2"));
        Response resp = executeRequest("/todos", HttpMethod.POST,
                Entity.json(new Todo("Test1","Desc1", tasks)));


        assertThat(resp.getStatus()).isEqualTo(HttpStatus.CREATED_201);
        Todo createdTodo = null;
        try {
             createdTodo = resp.readEntity(Todo.class);
        } catch (Exception e) {
            Assertions.fail();
        }
        assertThat(createdTodo.getId()).isNotNull();
        created = createdTodo;
        firstID = createdTodo.getId();

        resp = executeRequest("/todos", HttpMethod.POST, Entity.json(new Todo("Test2","Desc2")));
        assertThat(resp.getStatus()).isEqualTo(HttpStatus.CREATED_201);
        createdTodo = null;
        try {
            createdTodo = resp.readEntity(Todo.class);
        } catch (Exception e) {
            Assertions.fail();
        }
        assertThat(createdTodo.getId()).isNotNull();
        secondID = createdTodo.getId();
    }

    private Response executeRequest(String path, String method, Entity entity) {
        HttpAuthenticationFeature authFeature = HttpAuthenticationFeature.basic("integrationtest", "todosecret");
        Invocation.Builder builder = APP.client().target(HTTP_LOCALHOST + APP.getLocalPort() + path)
                .register(authFeature)
                .request(MediaType.APPLICATION_JSON_TYPE);

        switch (method) {
            case HttpMethod.GET:
                return builder.get();
            case HttpMethod.PUT:
                return builder.put(entity);
            case HttpMethod.POST:
                return builder.post(entity);
            case HttpMethod.DELETE:
                return builder.delete();
            default:
                return null;
        }
    }
}