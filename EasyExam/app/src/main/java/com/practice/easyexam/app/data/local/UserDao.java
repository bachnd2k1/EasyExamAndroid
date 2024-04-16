package com.practice.easyexam.app.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.practice.easyexam.app.model.Attempt;
import com.practice.easyexam.app.model.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertUser(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAttempt(Attempt attempt);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM user")
    List<User> observeAllUser();



    @Delete
    void deleteUser(User user);

    @Transaction
    @Query("SELECT DISTINCT *  FROM attempt WHERE email = :email")
    List<Attempt> getUserAndAttemptsWithSameEmail(String email);

    @Query("Select * from attempt where  subject = :name")
    Attempt getAttemp(String name);

    @Query("UPDATE attempt " +
            "SET createdTimeAttempt = :createdTime, correct = :correct, incorrect = :incorrect, earned = :earned" +
            " WHERE email =:email")
    void updateAttempt(long createdTime,int correct,int incorrect,int earned,String email);

    @Transaction
    @Query("SELECT SUM(earned) FROM attempt WHERE email = :email")
    int getOverAllPoints(String email);

}
