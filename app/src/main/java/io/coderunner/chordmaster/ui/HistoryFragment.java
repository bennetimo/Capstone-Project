package io.coderunner.chordmaster.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.coderunner.chordmaster.R;
import io.coderunner.chordmaster.data.HistoryAdapter;
import io.coderunner.chordmaster.data.HistoryHolder;
import io.coderunner.chordmaster.data.model.Score;
import io.coderunner.chordmaster.util.Constants;

public class HistoryFragment extends Fragment {

    @BindView(R.id.recyclerview_history) RecyclerView mRecyclerViewHistory;
    private Context mContext;

    private HistoryAdapter mHistoryAdapter;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUserId = mFirebaseUser.getUid();
        DatabaseReference scoresRef = mDatabase.child(Constants.getFirebaseLocationUsers(mContext)).child(mUserId).child(Constants.getFirebaseLocationScores(mContext));
        mHistoryAdapter = new HistoryAdapter(Score.class, R.layout.list_item_history, HistoryHolder.class, scoresRef);

        View root = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, root);
        mRecyclerViewHistory.setHasFixedSize(true);
        mRecyclerViewHistory.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerViewHistory.setAdapter(mHistoryAdapter);
        return root;
    }


}
