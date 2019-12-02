package com.github.deberth.todo.resources;

import com.github.deberth.todo.api.Todo;
import com.github.deberth.todo.core.TodoService;
import com.github.deberth.todo.core.TodoServiceResponse;
import com.github.deberth.todo.core.auth.User;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.*;

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
@Api(value = "/todos")
public class TodoResource {

    public static final String BASE_PATH_TODO = "/todos";
    private final TodoService todoService;

    public TodoResource(TodoService todoService) {
        this.todoService = todoService;
    }

    @GET
    @UnitOfWork
    @ApiOperation(value = "Returns all todos",
            response = Todo.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successful", response = Todo.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "Unauthorized user"),
            @ApiResponse(code = 500, message = "Internal error") })
    public Response findAllTodos(@ApiParam(hidden = true) @Auth User user) {
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
    @ApiOperation(value = "Returns todo for id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successful", response = Todo.class),
            @ApiResponse(code = 401, message = "Unauthorized user"),
            @ApiResponse(code = 404, message = "Could not find todo"),
            @ApiResponse(code = 500, message = "Internal error") })
    public Response findTodoById(
            @ApiParam(value = "Id of requested todo", required = true)
            @PathParam("id") int id,
            @ApiParam(hidden = true) @Auth User user) {
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
    @ApiOperation(value = "Creates new todo")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Todo has been created", response = Todo.class),
            @ApiResponse(code = 401, message = "Unauthorized user"),
            @ApiResponse(code = 409, message = "Todo already exists"),
            @ApiResponse(code = 500, message = "Internal error") })
    public Response createTodo(
            @ApiParam(value = "Todo to be persisted", required = true)
            @NotNull(message = "JSON body for creation must not be empty") @Valid Todo todo,
            @ApiParam(hidden = true) @Auth User user) throws URISyntaxException {

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
    @ApiOperation(value = "Removes todo by given id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Todo has been deleted"),
            @ApiResponse(code = 401, message = "Unauthorized user"),
            @ApiResponse(code = 404, message = "Could not find todo"),
            @ApiResponse(code = 500, message = "Internal error") })
    public Response removeTodo(
            @ApiParam(value = "Id of todo which shall be removed", required = true)
            @PathParam("id") int id,
            @ApiParam(hidden = true) @Auth User user) {

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
    @ApiOperation(value = "Removes todo by given id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Todo has been updated"),
            @ApiResponse(code = 201, message = "Todo has been created"),
            @ApiResponse(code = 401, message = "Unauthorized user"),
            @ApiResponse(code = 400, message = "Id in path is different than id in todo body"),
            @ApiResponse(code = 500, message = "Internal error") })
    public Response updateTodo(
            @ApiParam(value = "Id of todo which shall be updated", required = true)
            @PathParam("id") int id,
            @ApiParam(value = "Updated todo body", required = true)
            @Valid Todo updatedTodo,
            @Auth User user) throws URISyntaxException {

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
