package com.luanta.testspeechui.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "user_name")
    private String mName;

    public User(@NonNull String name) {
        this.mName = name;
    }

    @Ignore
    public User(int id, @NonNull String name) {
        this.id = id;
        this.mName = name;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(@NonNull String name) {
        this.mName = name;
    }
}
