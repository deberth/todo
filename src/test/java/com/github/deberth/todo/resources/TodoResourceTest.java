package com.github.deberth.todo.resources;

import com.github.deberth.todo.api.Todo;
import com.github.deberth.todo.core.TodoService;
import com.github.deberth.todo.core.TodoServiceDatabase;
import com.github.deberth.todo.core.TodoServiceResponse;
import com.github.deberth.todo.core.auth.TodoAuthenticator;
import com.github.deberth.todo.core.auth.User;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
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

	private static final TodoService SERVICE_DATABASE = mock(TodoServiceDatabase.class);
	private static final BasicCredentialAuthFilter<User> BASIC_AUTH_HANDLER =
			new BasicCredentialAuthFilter.Builder<User>()
					.setAuthenticator(new TodoAuthenticator())
					.setPrefix("Basic")
					.setRealm("Basic Auth")
					.buildAuthFilter();
	private static final HttpAuthenticationFeature AUTHENTICATION_FEATURE = HttpAuthenticationFeature.basic("integrationtest", "todosecret");
	private static final ResourceExtension RESOURCES = ResourceExtension.builder()
			.addResource(new TodoResource(SERVICE_DATABASE))
			.addProvider(new AuthDynamicFeature(BASIC_AUTH_HANDLER))
			.addProvider(new AuthValueFactoryProvider.Binder<>(User.class))
			.build();


	@BeforeEach
	void setUp() {
		reset(SERVICE_DATABASE);

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
		doReturn(expectedServiceResponse).when(SERVICE_DATABASE).findAllTodos();

		// when
		Response response = RESOURCES.target(TodoResource.BASE_PATH_TODO)
				.register(new AuthDynamicFeature(
					new BasicCredentialAuthFilter.Builder<User>()
						.setAuthenticator(new TodoAuthenticator())
						.setRealm("BASIC-AUTH-REALM")
						.buildAuthFilter()))
				.register(new AuthValueFactoryProvider.Binder<>(User.class))
				.register(AUTHENTICATION_FEATURE).request().get();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
		List<Todo> foundTodos = response.readEntity(new GenericType<List<Todo>>(){});
		List<Todo> expectedTodos = (List<Todo>) expectedServiceResponse.getEntity();
		assertThat(foundTodos.containsAll(expectedTodos)).isTrue();
		verify(SERVICE_DATABASE).findAllTodos();
	}

	@Test
	void findAllTodos_ServerErrorStatusCase() {

		// given
		TodoServiceResponse expectedServiceResponse = new TodoServiceResponse(999, null);

		// and
		doReturn(expectedServiceResponse).when(SERVICE_DATABASE).findAllTodos();

		// when
		Response response = RESOURCES.target(TodoResource.BASE_PATH_TODO).register(AUTHENTICATION_FEATURE).request().get();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		verify(SERVICE_DATABASE).findAllTodos();

	}

	@Test
	void findAllTodos_ServerErrorEntityCase() {

		// given
		TodoServiceResponse expectedServiceResponse = new TodoServiceResponse(TodoService.OK, null);

		// and
		doReturn(expectedServiceResponse).when(SERVICE_DATABASE).findAllTodos();

		// when
		Response response = RESOURCES.target(TodoResource.BASE_PATH_TODO).register(AUTHENTICATION_FEATURE).request().get();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
		verify(SERVICE_DATABASE).findAllTodos();

	}

	@Test
	void findTodoById_HappyCase() {

		// given
		int expectedTodoId = 111;
		Todo expectedTodo = new Todo(expectedTodoId, "","");
		TodoServiceResponse expectedServiceResponse = new TodoServiceResponse(TodoService.OK, expectedTodo);

		// and
		doReturn(expectedServiceResponse).when(SERVICE_DATABASE).findTodoById(expectedTodoId);

		// when
		Response response = RESOURCES.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).register(AUTHENTICATION_FEATURE).request().get();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
		assertThat(response.readEntity(Todo.class).equals(expectedTodo)).isTrue();
		verify(SERVICE_DATABASE).findTodoById(expectedTodoId);
	}

	@Test
	void findTodoById_ServerErrorStatusCase() {

		// given
		int expectedTodoId = 999;
		TodoServiceResponse expectedServiceResponse = new TodoServiceResponse(999, null);

		// and
		doReturn(expectedServiceResponse).when(SERVICE_DATABASE).findTodoById(expectedTodoId);

		// when
		Response response = RESOURCES.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).register(AUTHENTICATION_FEATURE).request().get();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		verify(SERVICE_DATABASE).findTodoById(expectedTodoId);
	}

	@Test
	void findTodoById_ServerErrorEntityCase() {

		// given
		int expectedTodoId = 33;
		TodoServiceResponse expectedServiceResponse = new TodoServiceResponse(TodoService.OK, null);

		// and
		doReturn(expectedServiceResponse).when(SERVICE_DATABASE).findTodoById(expectedTodoId);

		// when
		Response response = RESOURCES.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).register(AUTHENTICATION_FEATURE).request().get();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		verify(SERVICE_DATABASE).findTodoById(expectedTodoId);
	}

	@Test
	void findTodoById_NotFoundCase() {

		// given
		int expectedTodoId = 43;
		TodoServiceResponse expectedServiceResponse = new TodoServiceResponse(TodoService.NOT_FOUND, null);

		// and
		doReturn(expectedServiceResponse).when(SERVICE_DATABASE).findTodoById(expectedTodoId);

		// when
		Response response = RESOURCES.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).register(AUTHENTICATION_FEATURE).request().get();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
		verify(SERVICE_DATABASE).findTodoById(expectedTodoId);
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
		}).when(SERVICE_DATABASE).createNewTodo(expectedTodo);

		// when
		Response response = RESOURCES.target(TodoResource.BASE_PATH_TODO).register(AUTHENTICATION_FEATURE).request(MediaType.APPLICATION_JSON).post(Entity.json(expectedTodo));

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
		assertThat(response.readEntity(Todo.class).getId()).isEqualTo(expectedTodoId);
		verify(SERVICE_DATABASE).createNewTodo(Mockito.any());
	}

	@Test
	void createTodo_ConflictCase() {

		// given
		int expectedTodoId = 212;
		Todo expectedTodo = new Todo("test","");

		// and
		doReturn(new TodoServiceResponse(TodoService.CONFLICT, null)).when(SERVICE_DATABASE).createNewTodo(expectedTodo);

		// when
		Response response = RESOURCES.target(TodoResource.BASE_PATH_TODO).register(AUTHENTICATION_FEATURE).request(MediaType.APPLICATION_JSON).post(Entity.json(expectedTodo));

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.CONFLICT.getStatusCode());
		verify(SERVICE_DATABASE).createNewTodo(expectedTodo);
	}

	@Test
	void createTodo_ServerErrorEntityCase() {

		// given
		int expectedTodoId = 312;
		Todo expectedTodo = new Todo("test","");

		// and
		doReturn(new TodoServiceResponse(TodoService.CREATED, null)).when(SERVICE_DATABASE).createNewTodo(expectedTodo);

		// when
		Response response = RESOURCES.target(TodoResource.BASE_PATH_TODO).register(AUTHENTICATION_FEATURE).request(MediaType.APPLICATION_JSON).post(Entity.json(expectedTodo));

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		verify(SERVICE_DATABASE).createNewTodo(expectedTodo);
	}

	@Test
	void createTodo_ServerErrorStatusCase() {

		// given
		Todo expectedTodo = new Todo("test","");

		// and
		doReturn(new TodoServiceResponse(999, null)).when(SERVICE_DATABASE).createNewTodo(expectedTodo);

		// when
		Response response = RESOURCES.target(TodoResource.BASE_PATH_TODO).register(AUTHENTICATION_FEATURE).request(MediaType.APPLICATION_JSON).post(Entity.json(expectedTodo));

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		verify(SERVICE_DATABASE).createNewTodo(expectedTodo);
	}

	@Test
	void removeTodo_HappyCase() {

		// given
		int expectedTodoId = 411;

		// and
		doReturn(new TodoServiceResponse(TodoService.NO_CONTENT, null)).when(SERVICE_DATABASE).removeTodo(expectedTodoId);

		// when
		Response response = RESOURCES.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).register(AUTHENTICATION_FEATURE).request().delete();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
		verify(SERVICE_DATABASE).removeTodo(expectedTodoId);
	}



	@Test
	void removeTodo_NotFoundCase() {

		// given
		int expectedTodoId = 412;

		// and
		doReturn(new TodoServiceResponse(TodoService.NOT_FOUND, null)).when(SERVICE_DATABASE).removeTodo(expectedTodoId);

		// when
		Response response = RESOURCES.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).register(AUTHENTICATION_FEATURE).request().delete();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
		verify(SERVICE_DATABASE).removeTodo(expectedTodoId);
	}

	@Test
	void removeTodo_ServerErrorCase() {

		// given
		int expectedTodoId = 413;

		// and
		doReturn(new TodoServiceResponse(999, null)).when(SERVICE_DATABASE).removeTodo(expectedTodoId);

		// when
		Response response = RESOURCES.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).register(AUTHENTICATION_FEATURE).request().delete();

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		verify(SERVICE_DATABASE).removeTodo(expectedTodoId);
	}

	@Test
	void updateTodo_HappyCase() {

		// given
		int expectedTodoId = 511;
		Todo expectedTodo = new Todo("test","");

		// and
		doReturn(new TodoServiceResponse(TodoService.NO_CONTENT)).when(SERVICE_DATABASE).updateTodo(expectedTodoId, expectedTodo);

		// when
		Response response = RESOURCES.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).register(AUTHENTICATION_FEATURE).request(MediaType.APPLICATION_JSON).put(Entity.json(expectedTodo));

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
		verify(SERVICE_DATABASE).updateTodo(expectedTodoId, expectedTodo);
	}

	@Test
	void updateTodo_CreatedCase() {

		// given
		int expectedTodoId = 512;
		Todo expectedTodo = new Todo("test","");

		// and
		doReturn(new TodoServiceResponse(TodoService.CREATED, expectedTodo)).when(SERVICE_DATABASE).updateTodo(expectedTodoId, expectedTodo);

		// when
		Response response = RESOURCES.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).register(AUTHENTICATION_FEATURE).request(MediaType.APPLICATION_JSON).put(Entity.json(expectedTodo));

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
		assertThat(response.readEntity(Todo.class)).isNotNull();
		verify(SERVICE_DATABASE).updateTodo(expectedTodoId, expectedTodo);
	}

	@Test
	void updateTodo_CreatedServerErrorEntityCase() {

		// given
		int expectedTodoId = 512;
		Todo expectedTodo = new Todo("test","");

		// and
		doReturn(new TodoServiceResponse(TodoService.CREATED, null)).when(SERVICE_DATABASE).updateTodo(expectedTodoId, expectedTodo);

		// when
		Response response = RESOURCES.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).register(AUTHENTICATION_FEATURE).request(MediaType.APPLICATION_JSON).put(Entity.json(expectedTodo));

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		assertThat(response.readEntity(Todo.class)).isNull();
		verify(SERVICE_DATABASE).updateTodo(expectedTodoId, expectedTodo);
	}

	@Test
	void updateTodo_BadRequestCase() {

		// given
		Integer expectedTodoId = 513;
		Todo expectedTodo = new Todo(expectedTodoId,"test","");

		// and
		doReturn(new TodoServiceResponse(TodoService.BAD_REQUEST, null)).when(SERVICE_DATABASE).updateTodo(expectedTodoId, expectedTodo);

		// when
		Response response = RESOURCES.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).register(AUTHENTICATION_FEATURE).request(MediaType.APPLICATION_JSON).put(Entity.json(expectedTodo));

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
		verify(SERVICE_DATABASE).updateTodo(expectedTodoId, expectedTodo);
	}

	@Test
	void updateTodo_ServerErrorStatusCase() {

		// given
		Integer expectedTodoId = 513;
		Todo expectedTodo = new Todo(expectedTodoId,"test","");

		// and
		doReturn(new TodoServiceResponse(999, null)).when(SERVICE_DATABASE).updateTodo(expectedTodoId, expectedTodo);

		// when
		Response response = RESOURCES.target(TodoResource.BASE_PATH_TODO + "/" + expectedTodoId).register(AUTHENTICATION_FEATURE).request(MediaType.APPLICATION_JSON).put(Entity.json(expectedTodo));

		// then
		assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		verify(SERVICE_DATABASE).updateTodo(expectedTodoId, expectedTodo);
	}

}