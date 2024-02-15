package com.practice.easyexam.app.model;

import java.util.List;

public class RecordsResponse {
    private String message;
    private List<RecordTest> data;

    public RecordsResponse(String message, List<RecordTest> data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RecordTest> getData() {
        return data;
    }

    public void setData(List<RecordTest> data) {
        this.data = data;
    }
}
