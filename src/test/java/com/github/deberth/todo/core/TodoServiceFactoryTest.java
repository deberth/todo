package com.github.deberth.todo.core;

import com.github.deberth.todo.db.TaskDAOImpl;
import com.github.deberth.todo.db.TaskDAOImplLocal;
import com.github.deberth.todo.db.TodoDAOImpl;
import com.github.deberth.todo.db.TodoDAOImplLocal;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

class TodoServiceFactoryTest {

	@Test
	void getTodoService_Local() {
		TodoService todoService = TodoServiceFactory.getTodoService("local");
		assertThat(todoService).isInstanceOf(TodoServiceLocal.class);
		assertThat(todoService.taskDAO).isInstanceOf(TaskDAOImplLocal.class);
		assertThat(todoService.todoDAO).isInstanceOf(TodoDAOImplLocal.class);
	}

	@Test
	void getTodoService_Database() {
		TodoService todoService = TodoServiceFactory.getTodoService("database", mock(SessionFactory.class));
		assertThat(todoService).isInstanceOf(TodoServiceDatabase.class);
		assertThat(todoService.taskDAO).isInstanceOf(TaskDAOImpl.class);
		assertThat(todoService.todoDAO).isInstanceOf(TodoDAOImpl.class);
	}
}