package com.luanta.testspeechui.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "score_table")
public class Score {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "user_id")
    private int userId;

    @NonNull
    @ColumnInfo(name = "vowel_id")
    private int vowelId;

    @NonNull
    @ColumnInfo(name = "score")
    private int mScore;

    @NonNull
    @ColumnInfo(name = "time_stamp")
    private String mTimestamp;

    public Score(int userId, int vowelId, int score, @NonNull String timestamp) {
        this.userId = userId;
        this.vowelId = vowelId;
        this.mScore = score;
        this.mTimestamp = timestamp;
    }

    @Ignore
    public Score(int id, int userId, int vowelId, int score, @NonNull String timestamp) {
        this.id = id;
        this.userId = userId;
        this.vowelId = vowelId;
        this.mScore = score;
        this.mTimestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getVowelId() {
        return vowelId;
    }

    public int getScore() {
        return mScore;
    }

    @NonNull
    public String getTimestamp() {
        return mTimestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setVowelId(int vowelId) {
        this.vowelId = vowelId;
    }

    public void setScore(int score) {
        this.mScore = score;
    }

    public void setTimestamp(@NonNull String timestamp) {
        this.mTimestamp = timestamp;
    }
}
