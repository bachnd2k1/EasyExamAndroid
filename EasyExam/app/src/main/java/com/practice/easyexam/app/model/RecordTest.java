package com.practice.easyexam.app.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class RecordTest implements Serializable {
    public String idRoom;
    public String idQuestion;
    private String idStudent;
    private String nameStudent;
    private String state;
    private String answer;
    public String point;
    public String time;
    public String correctQuestion;
    public String currentQuestion;

    public String questions;
    public String day;
    public int isViewed;
    public RecordTest() {
    }

    public RecordTest(String idRoom, String idQuestion, String idStudent, String nameStudent, String state, String answer, String point, String time, String correctQuestion, String currentQuestion) {
        this.idRoom = idRoom;
        this.idQuestion = idQuestion;
        this.idStudent = idStudent;
        this.nameStudent = nameStudent;
        this.state = state;
        this.answer = answer;
        this.point = point;
        this.time = time;
        this.correctQuestion = correctQuestion;
        this.currentQuestion = currentQuestion;
    }

    public RecordTest(String idRoom, String idQuestion, String idStudent, String nameStudent, String state, String answer, String point, String time, String correctQuestion, String currentQuestion, String questions, String day) {
        this.idRoom = idRoom;
        this.idQuestion = idQuestion;
        this.idStudent = idStudent;
        this.nameStudent = nameStudent;
        this.state = state;
        this.answer = answer;
        this.point = point;
        this.time = time;
        this.correctQuestion = correctQuestion;
        this.currentQuestion = currentQuestion;
        this.questions = questions;
        this.day = day;
    }



    public RecordTest(String idRoom, String idQuestion, String idStudent, String nameStudent, String state, String answer, String point, String time, String correctQuestion, String currentQuestion, String questions, String day, int isViewed) {
        this.idRoom = idRoom;
        this.idQuestion = idQuestion;
        this.idStudent = idStudent;
        this.nameStudent = nameStudent;
        this.state = state;
        this.answer = answer;
        this.point = point;
        this.time = time;
        this.correctQuestion = correctQuestion;
        this.currentQuestion = currentQuestion;
        this.questions = questions;
        this.day = day;
        this.isViewed = isViewed;
    }

    public int getIsViewed() {
        return isViewed;
    }

    public void setIsViewed(int isViewed) {
        this.isViewed = isViewed;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(String idStudent) {
        this.idStudent = idStudent;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(String idRoom) {
        this.idRoom = idRoom;
    }

    public String getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(String idQuestion) {
        this.idQuestion = idQuestion;
    }

    public String getId() {
        return idStudent;
    }

    public void setId(String id) {
        this.idStudent = id;
    }

    public String getNameStudent() {
        return nameStudent;
    }

    public void setNameStudent(String nameStudent) {
        this.nameStudent = nameStudent;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCorrectQuestion() {
        return correctQuestion;
    }

    public void setCorrectQuestion(String correctQuestion) {
        this.correctQuestion = correctQuestion;
    }

    public String getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(String currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @NonNull
    @Override
    public String toString() {
        return "RecordTest{" +
                "idRoom='" + idRoom + '\'' +
                ", idQuestion='" + idQuestion + '\'' +
                ", idStudent='" + idStudent + '\'' +
                ", nameStudent='" + nameStudent + '\'' +
                ", state='" + state + '\'' +
                ", answer='" + answer + '\'' +
                ", point='" + point + '\'' +
                ", time='" + time + '\'' +
                ", correctQuestion='" + correctQuestion + '\'' +
                ", currentQuestion='" + currentQuestion + '\'' +
                ", questions='" + questions + '\'' +
                '}';
    }
}

