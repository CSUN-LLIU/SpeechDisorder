package com.luanta.testspeechui.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ScoreDao {

    // Add a new score
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Score score);

    // Select any one score
    @Query("SELECT * FROM score_table LIMIT 1")
    Score[] getAnyScore();

    // Get all scores
    @Query("SELECT * FROM score_table ORDER BY user_id ASC, vowel_id ASC, time_stamp ASC")
    LiveData<List<Score>> getAllScores();

    // Get all scores for a specific user
    @Query("SELECT * FROM score_table WHERE user_id = :user ORDER BY vowel_id ASC, time_stamp ASC")
    LiveData<List<Score>> getUserScores(int user);

    // Get all scores of a specific user for a specific vowel
    @Query("SELECT * FROM score_table WHERE user_id = :user AND vowel_id = :vowel ORDER BY time_stamp ASC")
    LiveData<List<Score>> getVowelScores(int user,int vowel);
}
