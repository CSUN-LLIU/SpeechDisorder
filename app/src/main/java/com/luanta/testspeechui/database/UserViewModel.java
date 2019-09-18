package com.luanta.testspeechui.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private AppRepository mRepository;
    private LiveData<List<User>> mAllUsers;

    public UserViewModel(@NonNull Application application) {
        super(application);
        mRepository = new AppRepository(application);
        mAllUsers = mRepository.getAllUsers();
    }

    // Add new user
    public void insert(User user){
        mRepository.insert(user);
    }

    // Delete a user
    public void delete(User user){
        mRepository.delete(user);
    }

    // Delete all users
    public void deleteAll(){
        mRepository.deleteAll();
    }

    // Update a user
    public void update(User user){
        mRepository.update(user);
    }
    // Get all users
    LiveData<List<User>> getAllUsers(){
        return mAllUsers;
    }
}
