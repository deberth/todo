package com.github.deberth.todo.db;

import com.github.deberth.todo.api.Task;
import com.github.deberth.todo.api.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TodoDAO_Impl_LocalTest {

    private static final TodoDAO DAO = new TodoDAOImplLocal();
    private static final Todo TODO_ONE = new Todo("Name1","Description1");
    private static final Todo TODO_TWO = new Todo("Name2","Description2");

    @BeforeEach
    void resetDAO() {
        DAO.findAll().parallelStream().forEach(t -> DAO.remove(t.getId()));
        
    }

    @Test
    void create() {
        
        TODO_TWO.setTasks(new HashSet<>(Arrays.asList(
                new Task("TaskName","TaskDescription"))));
        Todo createdOne = DAO.create(TODO_ONE);
        Todo createdTwo = DAO.create(TODO_TWO);

        assertThat(createdOne.getId() == 1);
        assertThat(createdOne.getName() == TODO_ONE.getName());
        assertThat(createdOne.getDescription() == TODO_ONE.getDescription());
        assertThat(createdOne.getTasks() == null);

        assertThat(createdTwo.getId() == 2);
        assertThat(createdTwo.getName() == createdTwo.getName());
        assertThat(createdTwo.getDescription() == TODO_TWO.getDescription());
        assertThat(createdTwo.getTasks().equals(TODO_TWO.getTasks()));

    }

    @Test
    void findAll() {
        List<Todo> todos = DAO.findAll();
        assertThat(todos.isEmpty()).isEqualTo(true);

        DAO.create(TODO_ONE);
        DAO.create(TODO_TWO);

        todos = DAO.findAll();
        assertThat(todos.size()).isEqualTo(2);
        assertThat(todos.contains(TODO_ONE)).isEqualTo(true);
        assertThat(todos.contains(TODO_TWO)).isEqualTo(true);
    }

    @Test
    void find() {
        Todo todo = DAO.find(1);
        assertThat(todo).isEqualTo(null);

        Todo created = DAO.create(TODO_ONE);
        todo = DAO.find(created.getId());
        assertThat(todo).isNotEqualTo(null);
        assertThat(todo).isEqualTo(created);
    }



    @Test
    void update() {
        Todo created = DAO.create(TODO_ONE);
        Todo updated = new Todo(created.getId(), created.getName(), created.getDescription(), created.getTasks());
        updated.setName("Updated");

        DAO.update(created.getId(), updated);
        Todo found = DAO.find(created.getId());

        assertThat(found.getId()).isEqualTo(updated.getId());
        assertThat(found.getName()).isNotEqualTo(created.getName());
        assertThat(found.getName()).isEqualTo(updated.getName());
    }

    @Test
    void remove() {
        Todo created = DAO.create(TODO_ONE);
        Todo found = DAO.find(created.getId());
        assertThat(found).isNotEqualTo(null);

        DAO.remove(found.getId());
        found = DAO.find(created.getId());
        assertThat(found).isEqualTo(null);
    }
}