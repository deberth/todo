package com.github.deberth.todo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TodoTest {

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	@Test
	public void serializesToJSON() throws Exception {
		final Todo todo = new Todo(1,"TestTodoName","TestTodoDescription",
				new HashSet<>(Arrays.asList(
						new Task("TestTaskName-1", "TestTaskDescription"),
						new Task(2, "TestTaskName-2", ""))));

		final String expected = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(
				MAPPER.readValue(fixture("fixtures/todo.json"), Todo.class));

		assertThat(todo.toString()).isEqualTo(expected);
	}

	@Test
	public void deserializesToTodo() throws Exception {
		final Todo todo = new Todo(1,"TestTodoName","TestTodoDescription",
				new HashSet<>(Arrays.asList(
						new Task("TestTaskName-1", "TestTaskDescription"),
						new Task(2, "TestTaskName-2", ""))));

		assertThat(MAPPER.readValue(fixture("fixtures/todo.json"), Todo.class))
				.isEqualTo(todo);
	}

}