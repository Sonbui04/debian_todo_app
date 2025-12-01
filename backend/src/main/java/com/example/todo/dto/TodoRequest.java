package com.example.todo.dto;

public class TodoRequest {

    private Long userId;
    private String title;
    private String description;
    private Boolean done;

    public TodoRequest() {
    }

    public TodoRequest(Long userId, String title, String description, Boolean done) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.done = done;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }
}
