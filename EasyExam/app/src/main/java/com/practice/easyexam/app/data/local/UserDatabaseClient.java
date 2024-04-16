package com.practice.easyexam.app.data.local;

import android.content.Context;

import androidx.room.Room;

public class UserDatabaseClient {

    private static final String DB_NAME = "user_db";
    private static final String DB_EXAM = "exam_db";
    private static UserDatabase userDatabase;
    private static ExamDatabase examDatabase;

    public static synchronized UserDatabase getInstance(Context context) {
        if (userDatabase == null) {
            userDatabase = Room.databaseBuilder(
                            context.getApplicationContext(), UserDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return userDatabase;
    }


    public static synchronized ExamDatabase getInstanceExam(Context context) {
        if (examDatabase == null) {
            examDatabase = Room.databaseBuilder(
                            context.getApplicationContext(), ExamDatabase.class, DB_EXAM)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return examDatabase;
    }


    public UserDatabase getUserDatabase() {
        return userDatabase;
    }


    public ExamDatabase getExamDatabase() {
        return examDatabase;
    }
}
