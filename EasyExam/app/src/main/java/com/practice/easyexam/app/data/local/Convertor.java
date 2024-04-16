package com.practice.easyexam.app.data.local;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.practice.easyexam.app.model.Attempt;

import java.util.List;

public class Convertor {

    @TypeConverter
    String fromList(List<Attempt> list){
        return new Gson().toJson(list);
    }

    @TypeConverter
    List<Attempt> toList(String json){
        return new Gson().fromJson(json, new TypeToken<List<Attempt>>(){}.getType());
    }


}
