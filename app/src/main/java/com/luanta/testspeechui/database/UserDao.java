package com.luanta.testspeechui.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {

    // Add a new user
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(User user);

    // Delete a user
    @Delete
    void delete(User user);

    // Delete all users
    @Query("DELETE FROM user_table")
    void deleteAll();

    // Update a user
    @Update
    void update(User... user);

    // Select any one user
    @Query("SELECT * FROM user_table LIMIT 1")
    User[] getAnyUser();

    // Get all users
    @Query("SELECT * FROM user_table ORDER BY user_name ASC")
    LiveData<List<User>> getAllUsers();
}
