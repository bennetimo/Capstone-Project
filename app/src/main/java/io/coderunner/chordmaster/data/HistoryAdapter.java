package io.coderunner.chordmaster.data;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

import io.coderunner.chordmaster.data.model.Score;

public class HistoryAdapter extends FirebaseRecyclerAdapter<Score, HistoryHolder> {

    public HistoryAdapter(Class<Score> modelClass, int modelLayout, Class<HistoryHolder> viewHolderClass, DatabaseReference ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    public void populateViewHolder(HistoryHolder historyHolder, Score score, int position) {
        historyHolder.setChordpair(score.getChangeString());
        historyHolder.setScore(score.getScore());
        historyHolder.setAchieved(score.getCreatedTimestamp());
    }
}