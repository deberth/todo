package com.github.deberth.todo.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

public class Task {

    @NotNull
    private Integer id;

    @NotBlank
    private String name;

    private String description;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Constructors
    //#######################################

    public Task() {}

    public Task(Integer id, String name) {
        super();
        this.id = id;
        this.name = name;
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
}
