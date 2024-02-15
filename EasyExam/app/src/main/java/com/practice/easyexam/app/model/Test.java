package com.practice.easyexam.app.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

@Entity(tableName = "test")
public class Test implements Serializable {
    @PrimaryKey
    @NotNull
    public String id =  UUID.randomUUID().toString();
    String name;
    ArrayList<Question> questionArrayList;
    String createDate;

    public Test() {
    }

    public Test(String name, ArrayList<Question> questionArrayList) {
        this.name = name;
        this.questionArrayList = questionArrayList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Question> getQuestionArrayList() {
        return questionArrayList;
    }

    public void setQuestionArrayList(ArrayList<Question> questionArrayList) {
        this.questionArrayList = questionArrayList;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    @NonNull
    @Override
    public String toString() {
        return "Test{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", questionArrayList=" + questionArrayList +
                ", createDate='" + createDate + '\'' +
                '}';
    }
}
