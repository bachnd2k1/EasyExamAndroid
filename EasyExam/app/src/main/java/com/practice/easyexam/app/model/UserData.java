package com.practice.easyexam.app.model;

public class UserData {
    private String id;
    private String name;
    private String state;
    private String options;

    public UserData(String id, String name, String state, String options) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.options = options;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public String getOptions() {
        return options;
    }
}
