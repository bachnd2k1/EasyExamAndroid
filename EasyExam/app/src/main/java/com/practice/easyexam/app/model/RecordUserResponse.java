package com.practice.easyexam.app.model;

import java.util.List;

public class RecordUserResponse {
    private String message;

    private List<RecordItem> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RecordItem> getData() {
        return data;
    }

    public void setData(List<RecordItem> data) {
        this.data = data;
    }
}

