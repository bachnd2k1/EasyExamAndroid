package com.practice.easyexam.app.model;

import java.io.Serializable;

public class Room implements Serializable {
    String idRoom;
    String nameRoom;
    int numOfUser;
    int time;
    String idTest;
    String idCreateUser;

    String createTime;

    int currentNumUser;

    String state;

    private int isViewed;
    public String startTime;

    public Room() {
    }

    public Room(String idRoom, String nameRoom, int numOfUser, int time, String idTest, String idCreateUser, String createTime) {
        this.idRoom = idRoom;
        this.nameRoom = nameRoom;
        this.numOfUser = numOfUser;
        this.time = time;
        this.idTest = idTest;
        this.idCreateUser = idCreateUser;
        this.createTime = createTime;
    }

    public Room(String idRoom, String nameRoom, int numOfUser, int time, String idTest, String idCreateUser) {
        this.idRoom = idRoom;
        this.nameRoom = nameRoom;
        this.numOfUser = numOfUser;
        this.time = time;
        this.idTest = idTest;
        this.idCreateUser = idCreateUser;
    }

    public Room(String idRoom, String nameRoom, int numOfUser, int time, String idTest, String idCreateUser, String createTime, int currentNumUser) {
        this.idRoom = idRoom;
        this.nameRoom = nameRoom;
        this.numOfUser = numOfUser;
        this.time = time;
        this.idTest = idTest;
        this.idCreateUser = idCreateUser;
        this.createTime = createTime;
        this.currentNumUser = currentNumUser;
    }

    public Room(String idRoom, String nameRoom, int numOfUser, int time, String idTest, String idCreateUser, String createTime, int currentNumUser, int isViewed) {
        this.idRoom = idRoom;
        this.nameRoom = nameRoom;
        this.numOfUser = numOfUser;
        this.time = time;
        this.idTest = idTest;
        this.idCreateUser = idCreateUser;
        this.createTime = createTime;
        this.currentNumUser = currentNumUser;
        this.isViewed = isViewed;
    }

    public Room(String idRoom, String nameRoom, int numOfUser, int time, String idTest, String idCreateUser, String createTime, int currentNumUser, String state, int isViewed) {
        this.idRoom = idRoom;
        this.nameRoom = nameRoom;
        this.numOfUser = numOfUser;
        this.time = time;
        this.idTest = idTest;
        this.idCreateUser = idCreateUser;
        this.createTime = createTime;
        this.currentNumUser = currentNumUser;
        this.state = state;
        this.isViewed = isViewed;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getCurrentNumUser() {
        return currentNumUser;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCurrentNumUser(int currentNumUser) {
        this.currentNumUser = currentNumUser;
    }

    public String getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(String idRoom) {
        this.idRoom = idRoom;
    }

    public String getNameRoom() {
        return nameRoom;
    }

    public void setNameRoom(String nameRoom) {
        this.nameRoom = nameRoom;
    }

    public int getNumOfUser() {
        return numOfUser;
    }

    public void setNumOfUser(int numOfUser) {
        this.numOfUser = numOfUser;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getIdTest() {
        return idTest;
    }

    public void setIdTest(String idTest) {
        this.idTest = idTest;
    }

    public String getIdCreateUser() {
        return idCreateUser;
    }

    public void setIdCreateUser(String idCreateUser) {
        this.idCreateUser = idCreateUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
