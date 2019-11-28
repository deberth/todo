package com.github.deberth.todo.resources;

import com.github.deberth.todo.api.Todo;
import com.github.deberth.todo.core.TodoService;
import com.github.deberth.todo.core.TodoServiceDatabase;
import com.github.deberth.todo.core.TodoServiceResponse;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(DropwizardExtensionsSupport.class)
class TodoResourceTest {

	private static final TodoService service = mock(TodoServiceDatabase.class);

	private static final ResourceExtension resources = ResourceExtension.builder()
			.addResource(new TodoResource(service))
			.build();

	@BeforeEach
	void setUp() {
		reset(service);

	}

	@Test
	void findAllTodos_HappyCase() {

		// given
		Todo todoOne = new Todo(11, "","");
		Todo todoTwo = new Todo(21, "","");
		List<Todo> todos = new java.util.ArrayList<>();
		todos.add(todoOne);
		todos.add(todoTwo);
		TodoServiceResponse expectedServiceResponse = new TodoServiceResponse(TodoService.OK, todos);

		// and
		doReturn(expectedServiceResponse).when(service).findAllTodos();

		// when
		Response response = resources.target(TodoResource.BASE_PATH_TODO).request().get();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
		List<Todo> foundTodos = response.readEntity(new GenericType<List<Todo>>(){});
		List<Todo> expectedTodos = (List<Todo>) expectedServiceResponse.getEntity();
		assertThat(foundTodos.containsAll(expectedTodos)).isTrue();
		verify(service).findAllTodos();
	}

	@Test
	void findAllTodos_ServerErrorStatusCase() {

		// given
		TodoServiceResponse expectedServiceResponse = new TodoServiceResponse(999, null);

		// and
		doReturn(expectedServiceResponse).when(service).findAllTodos();

		// when
		Response response = resources.target(TodoResource.BASE_PATH_TODO).request().get();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		verify(service).findAllTodos();

	}

	@Test
	void findAllTodos_ServerErrorEntityCase() {

		// given
		TodoServiceResponse expectedServiceResponse = new TodoServiceResponse(TodoService.OK, null);

		// and
		doReturn(expectedServiceResponse).when(service).findAllTodos();

		// when
		Response response = resources.target(TodoResource.BASE_PATH_TODO).request().get();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
		verify(service).findAllTodos();

	}

	@Test
	void findTodoById_HappyCase() {

		// given
		int expectedTodoId = 111;
		Todo expectedTodo = new Todo(expectedTodoId, "","");
		TodoServiceResponse expectedServiceResponse = new TodoServiceResponse(TodoService.OK, expectedTodo);

		// and
		doReturn(expectedServiceResponse).when(service).findTodoById(expectedTodoId);

		// when
		Response response = resources.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).request().get();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
		assertThat(response.readEntity(Todo.class).equals(expectedTodo)).isTrue();
		verify(service).findTodoById(expectedTodoId);
	}

	@Test
	void findTodoById_ServerErrorStatusCase() {

		// given
		int expectedTodoId = 999;
		TodoServiceResponse expectedServiceResponse = new TodoServiceResponse(999, null);

		// and
		doReturn(expectedServiceResponse).when(service).findTodoById(expectedTodoId);

		// when
		Response response = resources.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).request().get();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		verify(service).findTodoById(expectedTodoId);
	}

	@Test
	void findTodoById_ServerErrorEntityCase() {

		// given
		int expectedTodoId = 33;
		TodoServiceResponse expectedServiceResponse = new TodoServiceResponse(TodoService.OK, null);

		// and
		doReturn(expectedServiceResponse).when(service).findTodoById(expectedTodoId);

		// when
		Response response = resources.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).request().get();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		verify(service).findTodoById(expectedTodoId);
	}

	@Test
	void findTodoById_NotFoundCase() {

		// given
		int expectedTodoId = 43;
		TodoServiceResponse expectedServiceResponse = new TodoServiceResponse(TodoService.NOT_FOUND, null);

		// and
		doReturn(expectedServiceResponse).when(service).findTodoById(expectedTodoId);

		// when
		Response response = resources.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).request().get();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
		verify(service).findTodoById(expectedTodoId);
	}

	@Test
	void createTodo_HappyCase() {

		// given
		int expectedTodoId = 211;
		Todo expectedTodo = new Todo("test","");

		// and
		doAnswer(t -> {
			((Todo)t.getArgument(0)).setId(expectedTodoId);

			return new TodoServiceResponse(TodoService.CREATED, t.getArgument(0));
		}).when(service).createNewTodo(expectedTodo);

		// when
		Response response = resources.target(TodoResource.BASE_PATH_TODO).request(MediaType.APPLICATION_JSON).post(Entity.json(expectedTodo));

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
		assertThat(response.readEntity(Todo.class).getId()).isEqualTo(expectedTodoId);
		verify(service).createNewTodo(Mockito.any());
	}

	@Test
	void createTodo_ConflictCase() {

		// given
		int expectedTodoId = 212;
		Todo expectedTodo = new Todo("test","");

		// and
		doReturn(new TodoServiceResponse(TodoService.CONFLICT, null)).when(service).createNewTodo(expectedTodo);

		// when
		Response response = resources.target(TodoResource.BASE_PATH_TODO).request(MediaType.APPLICATION_JSON).post(Entity.json(expectedTodo));

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.CONFLICT.getStatusCode());
		verify(service).createNewTodo(expectedTodo);
	}

	@Test
	void createTodo_ServerErrorEntityCase() {

		// given
		int expectedTodoId = 312;
		Todo expectedTodo = new Todo("test","");

		// and
		doReturn(new TodoServiceResponse(TodoService.CREATED, null)).when(service).createNewTodo(expectedTodo);

		// when
		Response response = resources.target(TodoResource.BASE_PATH_TODO).request(MediaType.APPLICATION_JSON).post(Entity.json(expectedTodo));

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		verify(service).createNewTodo(expectedTodo);
	}

	@Test
	void createTodo_ServerErrorStatusCase() {

		// given
		Todo expectedTodo = new Todo("test","");

		// and
		doReturn(new TodoServiceResponse(999, null)).when(service).createNewTodo(expectedTodo);

		// when
		Response response = resources.target(TodoResource.BASE_PATH_TODO).request(MediaType.APPLICATION_JSON).post(Entity.json(expectedTodo));

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		verify(service).createNewTodo(expectedTodo);
	}

	@Test
	void removeTodo_HappyCase() {

		// given
		int expectedTodoId = 411;

		// and
		doReturn(new TodoServiceResponse(TodoService.NO_CONTENT, null)).when(service).removeTodo(expectedTodoId);

		// when
		Response response = resources.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).request().delete();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
		verify(service).removeTodo(expectedTodoId);
	}



	@Test
	void removeTodo_NotFoundCase() {

		// given
		int expectedTodoId = 412;

		// and
		doReturn(new TodoServiceResponse(TodoService.NOT_FOUND, null)).when(service).removeTodo(expectedTodoId);

		// when
		Response response = resources.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).request().delete();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
		verify(service).removeTodo(expectedTodoId);
	}

	@Test
	void removeTodo_ServerErrorCase() {

		// given
		int expectedTodoId = 413;

		// and
		doReturn(new TodoServiceResponse(999, null)).when(service).removeTodo(expectedTodoId);

		// when
		Response response = resources.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).request().delete();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		verify(service).removeTodo(expectedTodoId);
	}

	@Test
	void updateTodo_HappyCase() {

		// given
		int expectedTodoId = 511;
		Todo expectedTodo = new Todo("test","");

		// and
		doReturn(new TodoServiceResponse(TodoService.NO_CONTENT)).when(service).updateTodo(expectedTodoId, expectedTodo);

		// when
		Response response = resources.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).request(MediaType.APPLICATION_JSON).put(Entity.json(expectedTodo));

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
		verify(service).updateTodo(expectedTodoId, expectedTodo);
	}

	@Test
	void updateTodo_CreatedCase() {

		// given
		int expectedTodoId = 512;
		Todo expectedTodo = new Todo("test","");

		// and
		doReturn(new TodoServiceResponse(TodoService.CREATED, expectedTodo)).when(service).updateTodo(expectedTodoId, expectedTodo);

		// when
		Response response = resources.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).request(MediaType.APPLICATION_JSON).put(Entity.json(expectedTodo));

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
		assertThat(response.readEntity(Todo.class)).isNotNull();
		verify(service).updateTodo(expectedTodoId, expectedTodo);
	}

	@Test
	void updateTodo_CreatedServerErrorEntityCase() {

		// given
		int expectedTodoId = 512;
		Todo expectedTodo = new Todo("test","");

		// and
		doReturn(new TodoServiceResponse(TodoService.CREATED, null)).when(service).updateTodo(expectedTodoId, expectedTodo);

		// when
		Response response = resources.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).request(MediaType.APPLICATION_JSON).put(Entity.json(expectedTodo));

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		assertThat(response.readEntity(Todo.class)).isNull();
		verify(service).updateTodo(expectedTodoId, expectedTodo);
	}

	@Test
	void updateTodo_BadRequestCase() {

		// given
		Integer expectedTodoId = 513;
		Todo expectedTodo = new Todo(expectedTodoId,"test","");

		// and
		doReturn(new TodoServiceResponse(TodoService.BAD_REQUEST, null)).when(service).updateTodo(expectedTodoId, expectedTodo);

		// when
		Response response = resources.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).request(MediaType.APPLICATION_JSON).put(Entity.json(expectedTodo));

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
		verify(service).updateTodo(expectedTodoId, expectedTodo);
	}

	@Test
	void updateTodo_ServerErrorStatusCase() {

		// given
		Integer expectedTodoId = 513;
		Todo expectedTodo = new Todo(expectedTodoId,"test","");

		// and
		doReturn(new TodoServiceResponse(999, null)).when(service).updateTodo(expectedTodoId, expectedTodo);

		// when
		Response response = resources.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).request(MediaType.APPLICATION_JSON).put(Entity.json(expectedTodo));

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		verify(service).updateTodo(expectedTodoId, expectedTodo);
	}

}