package com.luanta.testspeechui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luanta.testspeechui.database.Score;

import java.util.List;

public class ScoreListAdapter extends RecyclerView.Adapter<ScoreListAdapter.ScoreViewHolder> {
    private final LayoutInflater mInflater;
    private List<Score> mScores; // cached copy of scores

    ScoreListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ScoreListAdapter.ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_score_item,parent,false);
        return new ScoreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreListAdapter.ScoreViewHolder holder, int position) {
        if(mScores != null){
            Score current = mScores.get(position);
//            holder.scoreItemUserId.setText(current.getUserId());
//            holder.scoreItemVowelId.setText(current.getVowelId());
            holder.scoreItemScore.setText("UserId: " + current.getUserId()
                    +" |VowelId: " + current.getVowelId() + " |Score: " + current.getScore()
                    + " |Time: " + current.getTimestamp());
//            holder.scoreItemTimestamp.setText(current.getTimestamp());
        }
        else {
            // no data available yet
//            holder.scoreItemUserId.setText("NoUser");
//            holder.scoreItemVowelId.setText("NoVowel");
            holder.scoreItemScore.setText("NoScore");
//            holder.scoreItemTimestamp.setText("NoTime");
        }
    }

    @Override
    public int getItemCount() {
        if(mScores != null) {
            return mScores.size();
        }
        else {
            return 0;
        }
    }

    void setScores(List<Score> scores) {
        mScores = scores;
        notifyDataSetChanged();
    }


    class ScoreViewHolder extends RecyclerView.ViewHolder{
//        private final TextView scoreItemUserId;
//        private final TextView scoreItemVowelId;
        private final TextView scoreItemScore;
//        private final TextView scoreItemTimestamp;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
//            scoreItemUserId = itemView.findViewById(R.id.textView_score_userId);
//            scoreItemVowelId = itemView.findViewById(R.id.textView_score_vowelId);
            scoreItemScore = itemView.findViewById(R.id.textView_score);
//            scoreItemTimestamp = itemView.findViewById(R.id.textView_score_time);
        }
    }
}
