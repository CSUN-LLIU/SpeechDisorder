package com.luanta.testspeechui.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ScoreViewModel extends AndroidViewModel {
    private AppRepository mRepository;
    private LiveData<List<Score>> mAllScores;

    public ScoreViewModel(@NonNull Application application) {
        super(application);
        mRepository = new AppRepository(application);
        mAllScores = mRepository.getAllScores();
    }

    // Add new score
    public void insert(Score score){
        mRepository.insertScore(score);
    }

    // Get all scores
    public LiveData<List<Score>> getAllScores(){
        return mAllScores;
    }
}
