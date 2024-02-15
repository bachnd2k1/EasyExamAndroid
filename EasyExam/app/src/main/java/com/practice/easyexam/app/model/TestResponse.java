package com.practice.easyexam.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private Map<String, Test> testData;

    public List<Test> getTests() {
        return new ArrayList<>(testData.values());
    }

    public void setTestData(Map<String, Test> testData) {
        this.testData = testData;
    }

    public String getMessage() {
        return message;
    }

    public void  setMessage(String message) {
        this.message = message;
    }
}
