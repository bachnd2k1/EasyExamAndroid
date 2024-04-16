package com.practice.easyexam.app.model;

import java.util.List;

public class UserResponse {
    private boolean success;
    private String message;
    private List<User> users;

    public UserResponse(boolean success, String message, List<User> users) {
        this.success = success;
        this.message = message;
        this.users = users;
    }

    public UserResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
