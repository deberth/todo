package com.github.deberth.todo.db;

import com.github.deberth.todo.api.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class TaskDAOImplTest {

	private static SessionFactory sessionFactory = mock(SessionFactory.class);
	private static final Session session = mock(Session.class);

	private static final TaskDAO DAO = new TaskDAOImpl(sessionFactory);

	@BeforeAll
	static void Setup() {
		doReturn(session).when(sessionFactory).getCurrentSession();
	}

	@BeforeEach
	void Reset() {
		reset(session);
	}

	@Test
	void findAll() {

		Query query = mock(Query.class);
		Task expected = new Task("name","description");
		doReturn(new ArrayList<Task>(Arrays.asList(expected))).when(query).list();
		doReturn(query).when(session).getNamedQuery(anyString());

		List<Task> tasks = DAO.findAll();
		assertThat(tasks.size()).isEqualTo(1);
		assertThat(tasks.get(0)).isEqualTo(expected);
	}

	@Test
	void find() {

		Task expected = new Task(1,"name","description");
		doReturn(expected).when(session).get(Task.class, expected.getId());

		Task actual = DAO.find(1);
		assertThat(actual).isNotEqualTo(null);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void create() {

		int expectedId = 5;
		Task expected = new Task("name","description");
		doAnswer(
				invocationOnMock -> {
					((Task)invocationOnMock.getArgument(0)).setId(expectedId);
					return null;
				}
		).when(session).saveOrUpdate(expected);
		Task created = DAO.create(expected);
		assertThat(created).isNotEqualTo(null);
		assertThat(created.getId()).isEqualTo(expectedId);
		assertThat(created).isEqualTo(expected);
	}

	@Test
	void update() {

		int expectedId = 9;
		String expected = "ChangedName";
		Task existing = new Task(expectedId, "name","description");
		doAnswer(
				invocationOnMock -> {
					((Task)invocationOnMock.getArgument(0)).setName(expected);
					return null;
				}
		).when(session).merge(existing);

		assertThat(existing.getName()).isEqualTo("name");
		DAO.update(expectedId, existing);
		assertThat(existing.getName()).isEqualTo(expected);
	}

	@Test
	void remove() {

		int expectedId = 12;
		Task expected = new Task(expectedId, "name","description");
		doReturn(expected).when(session).get(Task.class, expected.getId());
		doNothing().when(session).remove(expected);

		DAO.remove(expectedId);
		verify(session).remove(expected);
	}
}