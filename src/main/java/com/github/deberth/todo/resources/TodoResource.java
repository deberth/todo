package com.github.deberth.todo.resources;

import com.github.deberth.todo.api.Todo;
import com.github.deberth.todo.core.TodoService;
import com.github.deberth.todo.db.TaskDAO;
import com.github.deberth.todo.db.TodoDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

@Path("/todos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TodoResource {

    private final TodoService todoService;

    public TodoResource(TodoService todoService) {
        this.todoService = todoService;
    }

    @GET
    @UnitOfWork
    public Response findAllTodos() {
        return Response.ok(this.todoService.findAllTodos()).build();
    }

    @GET
    @UnitOfWork
    @Path("/{id}")
    public Response findTodoById(@PathParam("id") int id) {
        Todo todo = this.todoService.findTodoById(id);
        if (todo == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(todo).build();
    }

    @POST
    @UnitOfWork
    public Response createTodo(@Valid Todo todo) throws URISyntaxException {
        // TODO: Check if entity already exists?

        Todo createdTodo = this.todoService.createNewTodo(todo);
        // TODO: return URI or created todo?
        return Response.created(new URI("/todos/" + createdTodo.getId())).build();
    }

    @DELETE
    @UnitOfWork
    @Path("/{id}")
    public Response removeTodo(@PathParam("id") int id) {
        // Check for existing todo
        Todo todo = this.todoService.findTodoById(id);
        if (todo == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        this.todoService.removeTodo(id);
        return Response.noContent().build();
    }

    @PUT
    @UnitOfWork
    @Path("/{id}")
    public Response updateTodo(@PathParam("id") int id, @Valid @NotNull Todo updatedTodo) throws URISyntaxException {
        System.out.println("############### " + id);
        // Check for existing todo
        Todo todo = this.todoService.findTodoById(id);
        System.out.println("New" + updatedTodo.toString());

        if (todo == null) {
            Todo createdTodo = this.todoService.createNewTodo(updatedTodo);
            return Response.created(new URI("/todos/" + createdTodo.getId())).build();
        }
        System.out.println("Found" + todo.toString());
        this.todoService.updateTodo(updatedTodo);
        return Response.noContent().build();

    }

}
