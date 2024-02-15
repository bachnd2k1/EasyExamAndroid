package com.practice.easyexam.app.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.practice.easyexam.app.model.Test;

@Database(
        entities = {Test.class},
        exportSchema = false,
        version = 1
)
@TypeConverters(Converter.class)
public abstract class ExamDatabase extends RoomDatabase {
    public abstract ExamDao examDao();
}