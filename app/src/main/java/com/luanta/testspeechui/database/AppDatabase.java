package com.luanta.testspeechui.database;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {User.class},version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (AppDatabase.class){
                if(INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "test")
                            // Wipes and rebuilds instead of migrating if no Migration object
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // This callback is called when the database has opened.
    // In this case, use PopulateDbAsync to populate the database
    // with the initial data set if the database has no entries.
    private static Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){
                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                        new PopulateDbAsync(INSTANCE).execute();
                }
            };

    // Populate the database with the initial data set
    // only if the database has no entries.
    private static class PopulateDbAsync extends AsyncTask<Void,Void,Void> {
        private final UserDao mUserDao;

        // Initial data set
        private static String [] names = {"Child", "Female", "Male"};

        public PopulateDbAsync(AppDatabase db) {
            mUserDao = db.userDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // If we have no users, then create the initial list of users.
            if (mUserDao.getAnyUser().length < 1) {
                for (int i = 0; i <= names.length - 1; i++) {
                    User user = new User(names[i]);
                    mUserDao.insert(user);
                }
            }
            return null;
        }
    }
}

