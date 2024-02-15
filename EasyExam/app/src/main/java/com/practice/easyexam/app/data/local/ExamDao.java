package com.practice.easyexam.app.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.practice.easyexam.app.model.Test;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ExamDao {
    //    @Insert
//    long insertExam(Exam exam);
//
//    @Query("Select * from exam")
//    List<Exam> getListExam();
//
//    @Query("Select * from exam where  name = :name")
//    Exam getExam(String name);
//
//    @Query("SELECT EXISTS(SELECT * FROM exam)")
//    Boolean isExists();
    @Insert
    Completable insertExam(Test exam);

    @Query("SELECT * FROM Test")
    Flowable<List<Test>> getListExam();

    @Query("SELECT * FROM Test WHERE name = :name")
    Single<Test> getExam(String name);

    @Query("SELECT EXISTS(SELECT 1 FROM Test LIMIT 1)")
    Single<Boolean> isExists();


    @Query("SELECT * FROM Test WHERE name = :nameToCheck")
    Single<Test> getItemByName(String nameToCheck);
}
