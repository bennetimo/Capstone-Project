package io.coderunner.chordmaster.data;


import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.Query;

import java.util.ArrayList;

import io.coderunner.chordmaster.R;
import io.coderunner.chordmaster.data.model.Score;
import io.coderunner.chordmaster.ui.EmptyRecyclerView;
import io.coderunner.chordmaster.ui.FirebaseRecyclerAdapter;

public class HistoryAdapter extends FirebaseRecyclerAdapter<HistoryHolder, Score> {

    private EmptyRecyclerView mEmptyView;

    public HistoryAdapter(Query query, Class<Score> itemClass, @Nullable ArrayList<Score> items,
                          @Nullable ArrayList<String> keys, EmptyRecyclerView emptyView) {
        super(query, itemClass, items, keys);
        this.mEmptyView = emptyView;
    }

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_history, parent, false);
        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryHolder holder, int position) {
        Score score = getItem(position);
        holder.setScore(score.getScore());
        holder.setChordpair(score.getChange());
        holder.setAchieved(score.getCreatedTimestamp());
    }

    @Override
    protected void itemAdded(Score item, String key, int position) {
        mEmptyView.checkIfEmpty();
    }

    @Override
    protected void itemChanged(Score oldItem, Score newItem, String key, int position) {
        mEmptyView.checkIfEmpty();
    }

    @Override
    protected void itemRemoved(Score item, String key, int position) {
        mEmptyView.checkIfEmpty();
    }

    @Override
    protected void itemMoved(Score item, String key, int oldPosition, int newPosition) {
        mEmptyView.checkIfEmpty();
    }

}