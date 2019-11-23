package com.github.deberth.todo.api;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "todos")
@NamedQueries({
        @NamedQuery(name = "com.github.deberth.todo.api.Todo.findAll",
                query = "select t from Todo t")
})
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotBlank
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @OneToMany
    @JoinColumn(name = "todo_id")
    private List<Task> tasks;

    public void setId(Integer id) {this.id = id;}
    public Integer getId() {return this.id;}

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return this.description;
    }

    public List<Task> getTasks() {return tasks;}

    public void setTasks(List<Task> tasks) {this.tasks = tasks;}

    // Constructors
    //#######################################

    public Todo() {}

    public Todo(Integer id, String name, String description, List<Task> tasks) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.tasks = tasks;
    }

    // Methods
    //#######################################

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
                append(id).
                append(name).
                append(description).
                toHashCode();
    }


}
