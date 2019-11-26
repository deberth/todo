package com.github.deberth.todo.db;

import com.github.deberth.todo.api.Task;
import com.github.deberth.todo.api.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TaskDAO_Impl_LocalTest {

    private static final TaskDAO DAO = new TaskDAOImplLocal();
    private static final Task TASK_ONE = new Task("Name1","Description1");
    private static final Task TASK_TWO = new Task("Name2","Description2");

    @BeforeEach
    void resetDAO() {
        DAO.findAll().parallelStream().forEach(t -> DAO.remove(t.getId()));
        
    }

    @Test
    void create() {
        
        Task createdOne = DAO.create(TASK_ONE);
        Task createdTwo = DAO.create(TASK_TWO);

        assertThat(createdOne.getId() == 1);
        assertThat(createdOne.getName() == TASK_ONE.getName());
        assertThat(createdOne.getDescription() == TASK_ONE.getDescription());

        assertThat(createdTwo.getId() == 2);
        assertThat(createdTwo.getName() == createdTwo.getName());
        assertThat(createdTwo.getDescription() == TASK_TWO.getDescription());

    }

    @Test
    void findAll() {
        List<Task> tasks = DAO.findAll();
        assertThat(tasks.isEmpty()).isEqualTo(true);

        DAO.create(TASK_ONE);
        DAO.create(TASK_TWO);

        tasks = DAO.findAll();
        assertThat(tasks.size()).isEqualTo(2);
        assertThat(tasks.contains(TASK_ONE)).isEqualTo(true);
        assertThat(tasks.contains(TASK_TWO)).isEqualTo(true);
    }

    @Test
    void find() {
        Task task = DAO.find(1);
        assertThat(task).isEqualTo(null);

        Task created = DAO.create(TASK_ONE);
        task = DAO.find(created.getId());
        assertThat(task).isNotEqualTo(null);
        assertThat(task).isEqualTo(created);
    }



    @Test
    void update() {
        Task created = DAO.create(TASK_ONE);
        Task updated = new Task(created.getId(), created.getName(), created.getDescription());
        updated.setName("Updated");

        DAO.update(created.getId(), updated);
        Task found = DAO.find(created.getId());

        assertThat(found.getId()).isEqualTo(updated.getId());
        assertThat(found.getName()).isNotEqualTo(created.getName());
        assertThat(found.getName()).isEqualTo(updated.getName());
    }

    @Test
    void remove() {
        Task created = DAO.create(TASK_ONE);
        Task found = DAO.find(created.getId());
        assertThat(found).isNotEqualTo(null);

        DAO.remove(found.getId());
        found = DAO.find(created.getId());
        assertThat(found).isEqualTo(null);
    }
}