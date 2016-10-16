package io.coderunner.chordmaster.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.coderunner.chordmaster.R;
import io.coderunner.chordmaster.data.Score;

public class PracticeFragment extends Fragment {

    private CountDownTimer mPracticeTimer;
    @BindView(R.id.pbPractice) ProgressBar mPbPractice;
    @BindView(R.id.btnStop) Button mBtnStop;
    @BindView(R.id.tvTimeRemaining) TextView mTvTimeRemaining;
    @BindView(R.id.tvChordChange) TextView mTvChordChange;
    @BindView(R.id.tvPreviousBest) TextView mTvPreviousBest;
    @BindInt(R.integer.countdown_ms) int mCountdownMs;
    @BindInt(R.integer.leadin_ms) int mLeadinMs;
    @BindInt(R.integer.countdown_interval_ms) int mCountdownIntervalMs;
    @BindInt(R.integer.score_picker_max_value) int mScorePickerMax;
    @BindInt(R.integer.score_picker_min_value) int mScorePickerMin;
    @BindInt(R.integer.score_picker_default_value) int mScorePickerDefault;
    @BindString(R.string.pref_leadin_time_key) String mLeadinTimeKey;
    @BindString(R.string.pref_countdown_time_key) String mCountdownTimeKey;
    private Context mContext;
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUserId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_practice, container, false);
        ButterKnife.bind(this, root);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        int countdownMs = Integer.valueOf(sharedPref.getString(mCountdownTimeKey, "" + (mCountdownMs/1000)))*1000;
        int leadinMs = Integer.valueOf(sharedPref.getString(mLeadinTimeKey, "" + (mLeadinMs/1000)))*1000;

        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUserId = mFirebaseUser.getUid();

        int totalMs = countdownMs + leadinMs;
        int totalSeconds = totalMs / 1000;

        mPbPractice.setMax(totalSeconds);

        mDatabase.child("users").child(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Score previousBest = dataSnapshot.getValue(Score.class);
                mTvPreviousBest.setText("(Best: " + previousBest.score + ")");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Start the timer
        mPracticeTimer = new CountDownTimer(totalMs, mCountdownIntervalMs) {
            @Override
            public void onTick(long msTillFinished) {
                int progress = (int) (msTillFinished/1000);
                mPbPractice.setProgress(mPbPractice.getMax() - progress);
                mTvTimeRemaining.setText("" + progress);
            }

            @Override
            public void onFinish() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View dialogue = inflater.inflate(R.layout.dialogue_times_up, null);

                        final NumberPicker scorePicker = (NumberPicker) dialogue.findViewById(R.id.scorePicker);
                        scorePicker.setMaxValue(mScorePickerMax);
                        scorePicker.setMinValue(mScorePickerMin);
                        scorePicker.setValue(mScorePickerDefault);

                        builder.setView(dialogue);

                        builder.setMessage(R.string.dialogue_times_up_message)
                                .setTitle(R.string.dialogue_times_up_title);

                        // Add the buttons
                        builder.setPositiveButton(R.string.dialogue_times_up_ok_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Score score = new Score(mTvChordChange.getText().toString(), scorePicker.getValue(), System.currentTimeMillis());
                                mDatabase.child("users").child(mUserId).setValue(score);
                            }
                        });
                        builder.setNegativeButton(R.string.dialogue_times_up_cancel_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
        };
        mPracticeTimer.start();

        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPracticeTimer.cancel();
            }
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
