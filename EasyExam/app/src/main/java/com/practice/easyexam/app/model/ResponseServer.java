package com.practice.easyexam.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResponseServer<T> {

    private String message;


    private Map<String, T> data;

    public List<T> getDataList() {
        if (data != null) {
            return new ArrayList<>(data.values());
        } else {
            return new ArrayList<>();
        }
    }

    public Map<String, T> getDataMap() {
        return data;
    }

    public void setData(Map<String, T> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
