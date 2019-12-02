package com.github.deberth.todo.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "todos")
@NamedQueries({
        @NamedQuery(name = "com.github.deberth.todo.api.Todo.findAll",
                query = "select t from Todo t")
})
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description = "";

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    @JsonManagedReference
    private Set<Task> tasks = new HashSet<>();

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

    public Set<Task> getTasks() {return tasks;}

    public void setTasks(Set<Task> tasks) {this.tasks = tasks;}

    // Constructors
    //#######################################

    public Todo() {}

    public Todo(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Todo(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Todo(String name, String description, Set<Task> tasks) {
        this.name = name;
        this.description = description;
        this.tasks = tasks;
    }

    public Todo(Integer id, String name, String description, Set<Task> tasks) {
        this(name, description);
        this.id = id;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Todo)) {
            return false;
        }
        Todo toCompare = ((Todo) obj);
        return (this.id == null && toCompare.id == null) || (this.id.intValue() == toCompare.id.intValue()) &&
                this.name.equalsIgnoreCase(toCompare.name) &&
                this.description.equalsIgnoreCase(toCompare.description) &&
                this.tasks.equals(toCompare.tasks);

    }
}
