package com.github.deberth.todo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.jupiter.api.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TaskTest {

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	@Test
	public void serializesToJSON() throws Exception {
		final Task Task = new Task(1,"TestTaskName","TestTaskDescription");

		final String expected = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(
				MAPPER.readValue(fixture("fixtures/task.json"), Task.class));

		assertThat(Task.toString()).isEqualTo(expected);
	}

	@Test
	public void deserializesToTask() throws Exception {
		final Task Task = new Task(1,"TestTaskName","TestTaskDescription");

		assertThat(MAPPER.readValue(fixture("fixtures/task.json"), Task.class))
				.isEqualTo(Task);
	}

}