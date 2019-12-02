package com.github.deberth.todo.core;

import com.github.deberth.todo.api.Task;
import com.github.deberth.todo.api.Todo;
import com.github.deberth.todo.db.TaskDAO;
import com.github.deberth.todo.db.TodoDAO;
import com.google.common.base.Verify;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class TodoServiceLocalTest {

	private static final TodoDAO todoDAO = Mockito.mock(TodoDAO.class);
	private static final TaskDAO taskDAO = Mockito.mock(TaskDAO.class);

	private static final TodoService service = new TodoServiceLocal(todoDAO, taskDAO);


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
		Todo expected = new Todo(1, "name","description");
		doReturn(expected).when(todoDAO).find(expected.getId());

		// when
		TodoServiceResponse response = service.findTodoById(expected.getId());

		// then
		assertThat(response.getCode()).isEqualTo(TodoService.OK);
		assertThat(response.getEntity()).isNotNull();
		assertThat(response.getEntity()).isInstanceOf(Todo.class);
		assertThat(response.getEntity().equals(expected)).isTrue();
	}

	@Test
	void findTodoById_NotFoundCase() {

		// given
		Todo expected = new Todo(1, "name","description");
		doReturn(null).when(todoDAO).find(expected.getId());

		// when
		TodoServiceResponse response = service.findTodoById(expected.getId());

		// then
		assertThat(response.getCode()).isEqualTo(TodoService.NOT_FOUND);
		assertThat(response.getEntity()).isNull();
	}

	@Test
	void createNewTodo() {

		// given
		int expectedId = 5;
		Todo expected = new Todo( "name","description");
		Task taskOne = new Task("name1","description1");
		Task taskTwo = new Task("name2","description2");
		LinkedHashSet<Task> tasks = new LinkedHashSet<>();
		tasks.add(taskOne);
		tasks.add(taskTwo);
		expected.setTasks(tasks);

		// and
		doAnswer(t -> {
			((Todo)t.getArgument(0)).setId(expectedId);
			return t.getArgument(0);
		}).when(todoDAO).create(expected);
		doAnswer(t -> {
			((Task)t.getArgument(0)).setId(2);
			return t.getArgument(0);
		}).when(taskDAO).create(taskOne);
		doAnswer(t -> {
			((Task)t.getArgument(0)).setId(3);
			return t.getArgument(0);
		}).when(taskDAO).create(taskTwo);

		// when
		TodoServiceResponse response = service.createNewTodo(expected);

		// then
		assertThat(response.getCode()).isEqualTo(TodoService.CREATED);
		assertThat(response.getEntity()).isNotNull();
		assertThat(response.getEntity()).isInstanceOf(Todo.class);
		assertThat(((Todo)response.getEntity()).getId()).isEqualTo(expectedId);
		assertThat(((Todo)response.getEntity()).getTasks().size()).isEqualTo(2);
		assertThat(((Task)((Todo)response.getEntity()).getTasks().toArray()[0]).getId()).isEqualTo(2);
		assertThat(((Task)((Todo)response.getEntity()).getTasks().toArray()[1]).getId()).isEqualTo(3);
	}

	@Test
	void removeTodo() {

		// given
		int expectedTodoId = 15;
		int expectedTaskId = 28;
		Task existingTask = new Task(expectedTaskId, "name2","description2");
		LinkedHashSet<Task> tasks = new LinkedHashSet<>();
		tasks.add(existingTask);
		Todo expectedTodo = new Todo(expectedTodoId,"name","description", tasks);

		// and
		doReturn(expectedTodo).when(todoDAO).find(expectedTodoId);
		doNothing().when(taskDAO).remove(expectedTaskId);
		doNothing().when(todoDAO).remove(expectedTodoId);

		// when
		TodoServiceResponse response = service.removeTodo(expectedTodoId);

		// then
		assertThat(response.getCode()).isEqualTo(TodoService.NO_CONTENT);
		assertThat(response.getEntity()).isNull();
		verify(todoDAO).remove(expectedTodoId);
		verify(taskDAO).remove(expectedTaskId);

	}

	@Test
	void updateTodo() {

		// given
		int expectedTodoId = 42;
		int expectedTaskId = 33;
		int expectedNewTaskId = 55;

		Task existingTask = new Task(expectedTaskId, "name2","description2");
		Task newTask = new Task("name2","description2");
		LinkedHashSet<Task> tasks = new LinkedHashSet<>();
		tasks.add(existingTask);
		Todo expectedTodo = new Todo(expectedTodoId,"name","description", tasks);

		// and
		doReturn(expectedTodo).when(todoDAO).find(expectedTodoId);
		doNothing().when(taskDAO).remove(expectedTaskId);
		doAnswer(t -> {
			((Task)t.getArgument(0)).setId(expectedNewTaskId);
			return t.getArgument(0);
		}).when(taskDAO).create(newTask);
		doNothing().when(todoDAO).update(expectedTodoId, expectedTodo);

		// when
		TodoServiceResponse response = service.updateTodo(expectedTodoId, expectedTodo);

		// then
		assertThat(response.getCode()).isEqualTo(TodoService.NO_CONTENT);
		assertThat(response.getEntity()).isNull();
		verify(todoDAO).update(expectedTodoId, expectedTodo);
		verify(taskDAO).remove(expectedTaskId);
		verify(taskDAO).create(Mockito.any(Task.class));
	}
}