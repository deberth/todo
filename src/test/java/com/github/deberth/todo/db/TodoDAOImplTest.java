package com.github.deberth.todo.db;

import com.github.deberth.todo.api.Todo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class TodoDAOImplTest {

	private static SessionFactory sessionFactory = mock(SessionFactory.class);
	private static final Session session = mock(Session.class);

	private static final TodoDAO DAO = new TodoDAOImpl(sessionFactory);

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
		Todo expected = new Todo("name","description");
		doReturn(new ArrayList<Todo>(Arrays.asList(expected))).when(query).list();
		doReturn(query).when(session).getNamedQuery(anyString());

		List<Todo> todos = DAO.findAll();
		assertThat(todos.size()).isEqualTo(1);
		assertThat(todos.get(0)).isEqualTo(expected);
	}

	@Test
	void find() {

		Todo expected = new Todo(1,"name","description");
		doReturn(expected).when(session).get(Todo.class, expected.getId());

		Todo actual = DAO.find(1);
		assertThat(actual).isNotEqualTo(null);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void create() {

		int expectedId = 5;
		Todo expected = new Todo("name","description");
		doAnswer(
				invocationOnMock -> {
					((Todo)invocationOnMock.getArgument(0)).setId(expectedId);
					return null;
				}
		).when(session).saveOrUpdate(expected);
		Todo created = DAO.create(expected);
		assertThat(created).isNotEqualTo(null);
		assertThat(created.getId()).isEqualTo(expectedId);
		assertThat(created).isEqualTo(expected);
	}

	@Test
	void update() {

		int expectedId = 9;
		String expected = "ChangedName";
		Todo existing = new Todo(expectedId, "name","description");
		doAnswer(
				invocationOnMock -> {
					((Todo)invocationOnMock.getArgument(0)).setName(expected);
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
		Todo expected = new Todo(expectedId, "name","description");
		doReturn(expected).when(session).get(Todo.class, expected.getId());
		doNothing().when(session).remove(expected);

		DAO.remove(expectedId);
		verify(session).remove(expected);
	}
}