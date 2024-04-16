package com.practice.easyexam.app.model;

import com.google.gson.annotations.SerializedName;

public class ListQuestionResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("error")
    private Boolean error;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void  setMessage(String message) {
        this.message = message;
    }
}
