package com.practice.easyexam.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Question implements Serializable, Parcelable {
    private static final long serialVersionUID = 1L;
    String id;
    String idTest;

    String question;
    ArrayList<String> answers;
    int correctNum;
    private boolean expanded = false;
    private String image;
    private String path;


    public Question() {
    }

    public Question(String question, ArrayList<String> answers, int correctNum) {
        this.id =  UUID.randomUUID().toString();
        this.question = question;
        this.answers = answers;
        this.correctNum = correctNum;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getIdQuestion() {
        return id;
    }

    public void setIdQuestion() {
        this.id =  UUID.randomUUID().toString();
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIdTest() {
        return idTest;
    }

    public void setIdTest(String idTest) {
        this.idTest = idTest;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

    public int getCorrectNum() {
        return correctNum;
    }
    public void setCorrectNum(int correctNum) {
        this.correctNum = correctNum;
    }

    @Override
    public String toString() {
        return "Question{" +
                "idQuestion='" + id + '\'' +
                ", idTest='" + idTest + '\'' +
                ", question='" + question + '\'' +
                ", answers=" + answers +
                ", correctNum=" + correctNum +
                ",image=" + image +
                '}';
    }

    protected Question(Parcel in) {
        id = in.readString();
        idTest = in.readString(); // Read the idTest field
        question = in.readString();
        answers = in.createStringArrayList();
        correctNum = in.readInt();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(idTest); // Write the idTest field
        dest.writeString(question);
        dest.writeStringList(answers);
        dest.writeInt(correctNum);
        dest.writeString(image);
    }


    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}
