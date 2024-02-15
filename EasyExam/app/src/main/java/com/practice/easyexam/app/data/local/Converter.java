package com.practice.easyexam.app.data.local;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.practice.easyexam.app.model.Question;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Converter {
    @TypeConverter
    public static ArrayList<Question> fromString(String jsonString) {
        if (jsonString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Question>>() {}.getType();
        ArrayList<Question> questionList = gson.fromJson(jsonString,type);
        return questionList;
    }

    @TypeConverter
    public String fromCountryLangList(ArrayList<Question> questionArrayList) {
        if (questionArrayList == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Question>>() {}.getType();
        String json = gson.toJson(questionArrayList, type);
        return json;
    }
}
