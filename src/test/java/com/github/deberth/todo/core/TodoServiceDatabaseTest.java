package com.github.deberth.todo.core;

import com.github.deberth.todo.api.Task;
import com.github.deberth.todo.api.Todo;
import com.github.deberth.todo.db.TaskDAO;
import com.github.deberth.todo.db.TodoDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class TodoServiceDatabaseTest {

	private static final TodoDAO todoDAO = Mockito.mock(TodoDAO.class);
	private static final TaskDAO taskDAO = Mockito.mock(TaskDAO.class);

	private static final TodoService service = new TodoServiceDatabase(todoDAO, taskDAO);


	@BeforeEach
	void setup() {
		Mockito.reset(todoDAO);
		Mockito.reset(taskDAO);
	}

	@Test
	void findAllTodos() throws Exception {

		// given
		Todo expected = new Todo(1, "name","description");
		doReturn(
				new ArrayList<>(Arrays.asList(expected)))
				.when(todoDAO).findAll();

		// when
		TodoServiceResponse response = service.findAllTodos();

		// then
		assertThat(response.getCode()).isEqualTo(TodoService.OK);
		assertThat(response.getEntity()).isNotNull();
		assertThat(response.getEntity()).isInstanceOf(ArrayList.class);
		assertThat(((ArrayList<Todo>)response.getEntity()).size()).isEqualTo(1);
		assertThat(((ArrayList<Todo>)response.getEntity()).get(0)).isEqualTo(expected);
	}

	@Test
	void findTodoById_HappyCase() {

		// given
		int expectedTodoId = 1;
		Todo expected = new Todo(expectedTodoId, "name","description");
		doReturn(expected).when(todoDAO).find(expected.getId());

		// when
		TodoServiceResponse response = service.findTodoById(expectedTodoId);

		// then
		assertThat(response.getCode()).isEqualTo(TodoService.OK);
		assertThat(response.getEntity()).isNotNull();
		assertThat(response.getEntity()).isInstanceOf(Todo.class);
		assertThat(response.getEntity().equals(expected)).isTrue();
	}

	@Test
	void findTodoById_NotFoundCase() {

		// given
		int expectedTodoId = 3;
		doReturn(null).when(todoDAO).find(expectedTodoId);

		// when
		TodoServiceResponse response = service.findTodoById(expectedTodoId);

		// then
		assertThat(response.getCode()).isEqualTo(TodoService.NOT_FOUND);
		assertThat(response.getEntity()).isNull();
	}

	@Test
	void createNewTodo_HappyCase() {

		// given
		int expectedTodoId = 5;
		Todo expectedTodo = new Todo( "name","description");
		Task taskOne = new Task("name1","description1");
		Task taskTwo = new Task("name2","description2");
		LinkedHashSet<Task> tasks = new LinkedHashSet<>();
		tasks.add(taskOne);
		tasks.add(taskTwo);
		expectedTodo.setTasks(tasks);

		// and
		doReturn(null).when(todoDAO).find(expectedTodoId);
		doAnswer(t -> {
			((Todo)t.getArgument(0)).setId(expectedTodoId);
			return t.getArgument(0);
		}).when(todoDAO).create(expectedTodo);

		// when
		TodoServiceResponse response = service.createNewTodo(expectedTodo);

		// then
		assertThat(response.getCode()).isEqualTo(TodoService.CREATED);
		assertThat(response.getEntity()).isNotNull();
		assertThat(response.getEntity()).isInstanceOf(Todo.class);
		assertThat(((Todo)response.getEntity()).getId()).isEqualTo(expectedTodoId);
		assertThat(((Todo)response.getEntity()).getTasks().size()).isEqualTo(2);
	}

	@Test
	void createNewTodo_ConflictCase() {

		// given
		int expectedTodoId = 13;
		Todo expectedTodo = new Todo(expectedTodoId, "name","description");

		// and
		doReturn(expectedTodo).when(todoDAO).find(expectedTodoId);

		// when
		TodoServiceResponse response = service.createNewTodo(expectedTodo);

		// then
		assertThat(response.getCode()).isEqualTo(TodoService.CONFLICT);
		assertThat(response.getEntity()).isNull();
	}

	@Test
	void removeTodo_HappyCase() {

		// given
		int expectedTodoId = 15;
		Todo expectedTodo = new Todo(expectedTodoId, "name","description");

		// and
		doReturn(expectedTodo).when(todoDAO).find(expectedTodoId);
		doNothing().when(todoDAO).remove(expectedTodoId);

		// when
		TodoServiceResponse response = service.removeTodo(expectedTodoId);

		// then
		assertThat(response.getCode()).isEqualTo(TodoService.NO_CONTENT);
		assertThat(response.getEntity()).isNull();
		verify(todoDAO).remove(expectedTodoId);

	}

	@Test
	void removeTodo_NotFoundCase() {

		// given
		int expectedTodoId = 17;

		// and
		doReturn(null).when(todoDAO).find(expectedTodoId);

		// when
		TodoServiceResponse response = service.removeTodo(expectedTodoId);

		// then
		assertThat(response.getCode()).isEqualTo(TodoService.NOT_FOUND);
		assertThat(response.getEntity()).isNull();
		verify(todoDAO, never()).remove(expectedTodoId);

	}

	@Test
	void updateTodo_HappyCase() {

		// given
		int expectedTodoId = 46;
		int expectedTaskId = 36;
		Todo expectedTodo = new Todo(expectedTodoId,"name","description");

		// and
		doReturn(expectedTodo).when(todoDAO).find(expectedTodoId);
		doNothing().when(todoDAO).update(expectedTodoId, expectedTodo);

		// when
		TodoServiceResponse response = service.updateTodo(expectedTodoId, expectedTodo);

		// then
		assertThat(response.getCode()).isEqualTo(TodoService.NO_CONTENT);
		assertThat(response.getEntity()).isNull();
		verify(todoDAO).update(expectedTodoId, expectedTodo);
	}

	@Test
	void updateTodo_CreatedCase() {

		// given
		int expectedTodoId = 46;
		Todo expectedTodo = new Todo("name","description");

		// and
		doReturn(null).when(todoDAO).find(expectedTodoId);
		doNothing().when(todoDAO).update(expectedTodoId, expectedTodo);
		doAnswer(t -> {
			((Todo)t.getArgument(0)).setId(expectedTodoId);
			return t.getArgument(0);
		}).when(todoDAO).create(expectedTodo);

		// when
		TodoServiceResponse response = service.updateTodo(expectedTodoId, expectedTodo);

		// then
		assertThat(response.getCode()).isEqualTo(TodoService.CREATED);
		assertThat(response.getEntity()).isNotNull();
		verify(todoDAO).create(expectedTodo);
	}

	@Test
	void updateTodo_BadRequestCase() {

		// given
		int expectedTodoId = 56;
		int differentTodoId = 66;
		Todo expectedTodo = new Todo(differentTodoId,"name","description");

		// when
		TodoServiceResponse response = service.updateTodo(expectedTodoId, expectedTodo);

		// then
		assertThat(response.getCode()).isEqualTo(TodoService.BAD_REQUEST);
		assertThat(response.getEntity()).isNull();
		verify(todoDAO, never()).update(expectedTodoId, expectedTodo);
	}

}