package com.practice.easyexam.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuestionResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("questions")
    private List<Question> questions;

    public List<Question> getQuestions() {
        return questions;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}

