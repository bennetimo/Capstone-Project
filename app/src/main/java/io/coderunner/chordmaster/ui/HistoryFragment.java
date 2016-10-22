package io.coderunner.chordmaster.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    @BindView(R.id.recyclerview_history)
    EmptyRecyclerView mRecyclerViewHistory;
    @BindView(R.id.empty_data_view)
    TextView mEmptyView;
    private Context mContext;

    private HistoryAdapter mHistoryAdapter;
    private DatabaseReference mDatabase;

    private FirebaseUserProvider mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (FirebaseUserProvider) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement FirebaseUserProvider");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DatabaseReference scoresRef = mDatabase.child(Constants.getFirebaseLocationUsers(mContext)).child(mCallback.getFirebaseUser()).child(Constants.getFirebaseLocationScores(mContext));

        View root = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, root);
        mRecyclerViewHistory.setHasFixedSize(true);
        mRecyclerViewHistory.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerViewHistory.setAdapter(mHistoryAdapter);
        mRecyclerViewHistory.setEmptyView(mEmptyView);
        mHistoryAdapter = new HistoryAdapter(Score.class, R.layout.list_item_history, HistoryHolder.class, scoresRef, mRecyclerViewHistory);
        return root;
    }


}
