package com.practice.easyexam.app.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.practice.easyexam.app.model.Attempt;
import com.practice.easyexam.app.model.User;

@Database(
entities = {User.class, Attempt.class},
        exportSchema = false,
        version = 1
)

public abstract class UserDatabase extends RoomDatabase {

    public abstract UserDao userDao();

}


