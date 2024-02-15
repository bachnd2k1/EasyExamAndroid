package com.practice.easyexam.app.model;

public class RecordResponse {
    private String message;
    private RecordTest data;

    public RecordResponse(String message, RecordTest data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RecordTest getData() {
        return data;
    }

    public void setData(RecordTest data) {
        this.data = data;
    }
}
