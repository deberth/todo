package com.github.deberth.todo.core;

import com.github.deberth.todo.api.Todo;

public class TodoServiceResponse {
    private Object entity;
    private int code;

    public TodoServiceResponse(int code) {
        this.code = code;
    }

    public TodoServiceResponse(int code, Object entity) {
        this(code);
        this.entity = entity;
    }

    public Object getEntity() {
        return this.entity;
    }

    public int getCode() {
        return this.code;
    }
}
