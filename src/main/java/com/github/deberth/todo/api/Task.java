package com.github.deberth.todo.api;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
@Entity
@Table(name = "tasks")
@NamedQueries({
        @NamedQuery(name = "com.github.deberth.todo.api.Task.findAll",
                query = "select t from Task t")
})
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", insertable = false, updatable = false)
    @JsonBackReference
    private Todo todo;

    public void setId(Integer id) {this.id = id;}
    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

    public Todo getTodo() {
        return todo;
    }

    public void setTodo(Todo todo) {
        this.todo = todo;
    }

    // Constructors
    //#######################################

    public Task() {}

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(Integer id, String name, String description) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
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

        if (!(obj instanceof Task)) {
            return false;
        }
        if (this.id == ((Task) obj).id  &&
            this.name.equalsIgnoreCase(((Task) obj).name)    &&
            this.description.equalsIgnoreCase(((Task) obj).description)) {
                        return true;
        } else {
            return false;
        }

    }
}
