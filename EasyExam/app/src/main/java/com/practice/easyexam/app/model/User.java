package com.practice.easyexam.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User implements Parcelable {

    @ColumnInfo(name = "name")
    private final String name;
    @NonNull
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "email")
    private final String email;
    @ColumnInfo(name = "password")
    private String password;
    @ColumnInfo(name = "idClass")
    private String idClass;
    @ColumnInfo(name = "role")
    private String role;
    @ColumnInfo(name = "idStudent")
    private String idStudent;

//
//    public User(String name, @NonNull String email, String password) {
//        this.name = name;
//        this.email = email;
//        this.password = password;
//    }

//    public User(String name, @NonNull String email, String password, String idClass, String role) {
//        this.name = name;
//        this.email = email;
//        this.password = password;
//        this.idClass = idClass;
//        this.role = role;
//    }


    public User(String name, @NonNull String email, String password, String idClass, String role, String idStudent) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.idClass = idClass;
        this.role = role;
        this.idStudent = idStudent;
    }

    public String getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(String idStudent) {
        this.idStudent = idStudent;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getIdClass() {
        return idClass;
    }

    public void setIdClass(String idClass) {
        this.idClass = idClass;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public static Creator<User> getCREATOR() {
        return CREATOR;
    }

    public User(Parcel in) {
        name = in.readString();
        email = in.readString();
        password = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(password);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
