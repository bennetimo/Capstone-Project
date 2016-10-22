package io.coderunner.chordmaster.data;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

import io.coderunner.chordmaster.data.model.Score;
import io.coderunner.chordmaster.ui.EmptyRecyclerView;

public class HistoryAdapter extends FirebaseRecyclerAdapter<Score, HistoryHolder> {

    private EmptyRecyclerView mEmptyRecyclerView;

    public HistoryAdapter(Class<Score> modelClass, int modelLayout, Class<HistoryHolder> viewHolderClass, DatabaseReference ref, EmptyRecyclerView emptyRecyclerView) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mEmptyRecyclerView = emptyRecyclerView;
    }

    @Override
    public void populateViewHolder(HistoryHolder historyHolder, Score score, int position) {
        historyHolder.setChordpair(score.getChange());
        historyHolder.setScore(score.getScore());
        historyHolder.setAchieved(score.getCreatedTimestamp());
        mEmptyRecyclerView.checkIfEmpty();
    }

}