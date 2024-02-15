package com.practice.easyexam.app.model;

import java.io.Serializable;

public class RecordItem implements Serializable {

    private RecordTest recordTest;

    private String nameRoom;

    // getters and setters


    public RecordTest getRecordTest() {
        return recordTest;
    }

    public void setRecordTest(RecordTest recordTest) {
        this.recordTest = recordTest;
    }

    public String getNameRoom() {
        return nameRoom;
    }

    public void setNameRoom(String nameRoom) {
        this.nameRoom = nameRoom;
    }
}
