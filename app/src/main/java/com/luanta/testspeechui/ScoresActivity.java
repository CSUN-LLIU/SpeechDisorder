package com.luanta.testspeechui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.luanta.testspeechui.database.Score;
import com.luanta.testspeechui.database.ScoreViewModel;

import java.util.List;

public class ScoresActivity extends AppCompatActivity {
    private ScoreViewModel mScoreViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        // Set up the RecyclerView.
        RecyclerView recyclerViewScores = findViewById(R.id.recyclerview_scores);
        final ScoreListAdapter adapterScores = new ScoreListAdapter(this);
        recyclerViewScores.setAdapter(adapterScores);
        recyclerViewScores.setLayoutManager(new LinearLayoutManager(this));

        // Set up the UserViewModel
        mScoreViewModel = ViewModelProviders.of(this).get(ScoreViewModel.class);

        // Get all the scores from the database
        // and associate them to the adapter
        mScoreViewModel.getAllScores().observe(this, new Observer<List<Score>>() {
            @Override
            public void onChanged(@Nullable final List<Score> scores) {
                // Update the cached copy of the users in the adapter
                adapterScores.setScores(scores);
            }
        });
    }
}
