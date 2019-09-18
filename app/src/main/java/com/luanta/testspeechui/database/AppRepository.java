package com.luanta.testspeechui.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class AppRepository {
    private UserDao mUserDao;
    private LiveData<List<User>> mAllUsers;

    public AppRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mUserDao = db.userDao();
        mAllUsers = mUserDao.getAllUsers();
    }

    // Add a new user
    public void insert(User user){
        new insertAsyncTask(mUserDao).execute(user);
    }

    // Delete a user
    public void delete(User user){
        new deleteAsyncTask(mUserDao).execute(user);
    }

    // Delete all users
    public void deleteAll(){
        new deleteAllAsyncTask(mUserDao).execute();
    }

    // Update a user
    public void update(User user){
        new updateAsyncTask(mUserDao).execute(user);
    }

    public LiveData<List<User>> getAllUsers(){
        return mAllUsers;
    }

    // Add a new user (AsyncTask)
    private static class insertAsyncTask extends AsyncTask<User,Void,Void> {
        private UserDao mAsyncUserDao;

        public insertAsyncTask(UserDao userDao) {
            this.mAsyncUserDao = userDao;
        }

        @Override
        protected Void doInBackground(final User... users) {
            mAsyncUserDao.insert(users[0]);
            return null;
        }
    }

    // Delete a user (AsyncTask)
    private static class deleteAsyncTask extends AsyncTask<User,Void,Void> {
        private UserDao mAsyncUserDao;

        public deleteAsyncTask(UserDao userDao) {
            this.mAsyncUserDao = userDao;
        }

        @Override
        protected Void doInBackground(final User... users) {
            mAsyncUserDao.delete(users[0]);
            return null;
        }
    }

    // Delete all users (AsyncTask)
    private static class deleteAllAsyncTask extends AsyncTask<Void,Void,Void> {
        private UserDao mAsyncUserDao;

        public deleteAllAsyncTask(UserDao userDao) {
            this.mAsyncUserDao = userDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncUserDao.deleteAll();
            return null;
        }
    }

    // Update a user (AsyncTask)
    private static class updateAsyncTask extends AsyncTask<User,Void,Void> {
        private UserDao mAsyncUserDao;

        public updateAsyncTask(UserDao userDao) {
            this.mAsyncUserDao = userDao;
        }

        @Override
        protected Void doInBackground(final User... users) {
            mAsyncUserDao.update(users[0]);
            return null;
        }
    }
}
