package com.github.deberth.todo.resources;

import com.github.deberth.todo.api.Todo;
import com.github.deberth.todo.core.TodoService;
import com.github.deberth.todo.core.TodoServiceResponse;
import io.dropwizard.hibernate.UnitOfWork;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Path(TodoResource.BASE_PATH_TODO)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TodoResource {

    public static final String BASE_PATH_TODO = "/todos";
    private final TodoService todoService;

    public TodoResource(TodoService todoService) {
        this.todoService = todoService;
    }

    @GET
    @UnitOfWork
    public Response findAllTodos() {
        TodoServiceResponse serviceResponse = this.todoService.findAllTodos();

        if (serviceResponse.getCode() == TodoService.OK) {
            Object foundTodos = serviceResponse.getEntity();
            if (foundTodos != null && foundTodos instanceof List) {
                return Response.ok(foundTodos).build();
            } else {
                return Response.ok().build();
            }
        }
        return Response.serverError().build();
    }

    @GET
    @UnitOfWork
    @Path("/{id}")
    public Response findTodoById(@PathParam("id") int id) {
        TodoServiceResponse serviceResponse = this.todoService.findTodoById(id);

        switch (serviceResponse.getCode()) {
            case TodoService.OK:
                Object foundTodo = serviceResponse.getEntity();
                if (foundTodo instanceof Todo) {
                    return Response.ok(foundTodo).build();
                } else {
                    return Response.serverError().build();
                }
                case TodoService.NOT_FOUND:
                    return Response.status(Response.Status.NOT_FOUND).build();
            default:
                return Response.serverError().build();
        }
    }

    @POST
    @UnitOfWork
    public Response createTodo(@Valid Todo todo) throws URISyntaxException {

        TodoServiceResponse serviceResponse = this.todoService.createNewTodo(todo);

        switch (serviceResponse.getCode()) {
            case TodoService.CONFLICT:
                return Response.status(Response.Status.CONFLICT).build();
            case TodoService.CREATED:
                Object createdTodo = serviceResponse.getEntity();
                if (createdTodo instanceof Todo) {
                    return Response.created(new URI(BASE_PATH_TODO + ((Todo) createdTodo).getId())).entity(createdTodo).build();
                } else {
                    return Response.serverError().build();
                }
            default:
                return Response.serverError().build();
        }
    }

    @DELETE
    @UnitOfWork
    @Path("/{id}")
    public Response removeTodo(@PathParam("id") int id) {

        TodoServiceResponse serviceResponse = this.todoService.removeTodo(id);;

        switch (serviceResponse.getCode()) {
            case TodoService.NOT_FOUND:
                return Response.status(Response.Status.NOT_FOUND).build();
            case TodoService.NO_CONTENT:
                return Response.noContent().build();
            default:
                return Response.serverError().build();
        }
    }

    @PUT
    @UnitOfWork
    @Path("/{id}")
    public Response updateTodo(@PathParam("id") int id, @Valid @NotNull Todo updatedTodo) throws URISyntaxException {

        TodoServiceResponse serviceResponse = this.todoService.updateTodo(id, updatedTodo);;

        switch (serviceResponse.getCode()) {
            case TodoService.BAD_REQUEST:
                return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
            case TodoService.CREATED:
                Object createdTodo = serviceResponse.getEntity();
                if (createdTodo instanceof Todo) {
                    return Response.created(new URI(BASE_PATH_TODO + ((Todo) createdTodo).getId())).entity(createdTodo).build();
                } else {
                    return Response.serverError().build();
                }
            case TodoService.NO_CONTENT:
                return Response.noContent().build();
            default:
                return Response.serverError().build();
        }
    }

}
