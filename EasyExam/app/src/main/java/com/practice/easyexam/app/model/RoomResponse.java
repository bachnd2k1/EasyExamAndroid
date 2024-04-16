package com.practice.easyexam.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomResponse {
    private List<Room> data;
    private String message;

    public List<Room> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
