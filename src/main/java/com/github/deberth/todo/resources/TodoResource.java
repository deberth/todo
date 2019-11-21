package com.github.deberth.todo.resources;

import com.github.deberth.todo.api.Todo;
import com.github.deberth.todo.db.TodoDAO;
import com.github.deberth.todo.db.TodoDAO_Local;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/todos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TodoResource {

    private final Validator validator;

    // private final TaskDAO taskDAO;
    private final TodoDAO todoDAO;

    public TodoResource(TodoDAO todoDAO, Validator validator) {
        this.todoDAO = todoDAO;
        this.validator = validator;
    }

    @GET
    public Response findAllTodos() {
        return Response.ok(this.todoDAO.findAll()).build();
    }

    @GET
    @Path("/{id}")
    public Response findTodoById(@PathParam("id") int id) {
        Todo todo = this.todoDAO.find(id);
        if (todo == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(todo).build();
    }

    @POST
    public Response createTodo(@Valid Todo newTodo) throws URISyntaxException {
        // Validate input
        Set<ConstraintViolation<Todo>> constraintViolations = validator.validate(newTodo);
        if (!constraintViolations.isEmpty()) {
            List<String> validationMessages = constraintViolations.stream().map(v ->  v.getPropertyPath() + ":" + v.getMessage()).collect(Collectors.toList());
            return Response.status(Response.Status.BAD_REQUEST).entity(validationMessages).build();
        }

        // TODO: Check if entity already exists?

        Todo createdTodo = this.todoDAO.create(newTodo);
        // TODO: return URI or created todo?
        return Response.created(new URI("/todos/" + createdTodo.getId())).build();
    }

    @DELETE
    @Path("/{id}")
    public Response removeTodo(@PathParam("id") int id) {
        // Check for existing todo
        Todo todo = this.todoDAO.find(id);
        if (todo == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        this.todoDAO.remove(id);
        return Response.noContent().build();
    }

    @PUT
    @PathParam("{id}")
    public Response updateTodo(@PathParam("id") int id, Todo updatedTodo) throws URISyntaxException {
        // Validate input
        Set<ConstraintViolation<Todo>> constraintViolations = validator.validate(updatedTodo);
        if (!constraintViolations.isEmpty()) {
            List<String> validationMessages = constraintViolations.stream().map(v ->  v.getPropertyPath() + ":" + v.getMessage()).collect(Collectors.toList());
            return Response.status(Response.Status.BAD_REQUEST).entity(validationMessages).build();
        }

        // Check for existing todo
        Todo todo = this.todoDAO.find(id);
        if (todo == null) {
            Todo createdTodo = this.todoDAO.create(updatedTodo);
            return Response.created(new URI("/todos/" + createdTodo.getId())).build();
        }

        this.todoDAO.update(id, updatedTodo);
        return Response.noContent().build();

    }

}
