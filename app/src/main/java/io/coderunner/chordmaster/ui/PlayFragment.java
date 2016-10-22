package io.coderunner.chordmaster.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.parceler.Parcels;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.coderunner.chordmaster.R;
import io.coderunner.chordmaster.data.model.Change;
import io.coderunner.chordmaster.data.model.Chord;
import io.coderunner.chordmaster.data.model.Score;
import io.coderunner.chordmaster.util.Constants;

public class PlayFragment extends Fragment {

    private final String LOG_TAG = this.getClass().getSimpleName();
    @BindView(R.id.pbPractice)
    ProgressBar mPbPractice;
    @BindView(R.id.btnPause)
    FloatingActionButton mBtnPause;
    @BindView(R.id.tvTimeRemaining)
    TextView mTvTimeRemaining;
    @BindView(R.id.tvChordChange)
    TextView mTvChordChange;
    @BindView(R.id.tvPreviousBest)
    TextView mTvPreviousBest;
    @BindView(R.id.btnPlay)
    FloatingActionButton mBtnPlay;
    @BindInt(R.integer.countdown_ms)
    int mCountdownMs;
    @BindInt(R.integer.leadin_ms)
    int mLeadinMs;
    @BindInt(R.integer.countdown_interval_ms)
    int mCountdownIntervalMs;
    @BindInt(R.integer.score_picker_max_value)
    int mScorePickerMax;
    @BindInt(R.integer.score_picker_min_value)
    int mScorePickerMin;
    @BindInt(R.integer.score_picker_default_value)
    int mScorePickerDefault;
    @BindString(R.string.pref_leadin_time_key)
    String mLeadinTimeKey;
    @BindString(R.string.pref_countdown_time_key)
    String mCountdownTimeKey;
    private CountDownTimer mPracticeTimer;
    private Context mContext;
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUserId;
    private Change change;

    private long millisRemaining;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        change = Parcels.unwrap(getActivity().getIntent().getParcelableExtra(Intent.EXTRA_TEXT));
        change = new Change(new Chord("A"), new Chord("Am"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_play, container, false);
        ButterKnife.bind(this, root);

        mTvChordChange.setText(change.getChangeString());
        mTvChordChange.setContentDescription(change.getChord1().getName() + " to " + change.getChord2().getName());

        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUserId = mFirebaseUser.getUid();

        mDatabase.child(Constants.getFirebaseLocationUsers(mContext)).child(mUserId).child(Constants.getFirebaseLocationScores(mContext)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Score previousBest = dataSnapshot.getValue(Score.class);
                if (previousBest != null) {
                    String best = String.format(mContext.getString(R.string.format_best_score), previousBest.score);
                    mTvPreviousBest.setText(best);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        toggleFab(mBtnPause);
        mBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPracticeTimer.cancel();
                toggleFab(mBtnPause);
                toggleFab(mBtnPlay);
            }
        });

        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPlay();
                toggleFab(mBtnPause);
                toggleFab(mBtnPlay);
            }
        });
        mBtnPause.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.background)));

        return root;
    }

    private void toggleFab(FloatingActionButton fab){
        if(fab.isEnabled()){
            // Disable it
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.background)));
            fab.setEnabled(false);
        } else {
            // Enable it
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightBlueA400)));
            fab.setEnabled(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPracticeTimer != null) {
            Log.d(LOG_TAG, "Cancelling timer as fragment is paused");
            mPracticeTimer.cancel();
        }
    }

    public void addScore(int score) {
        DatabaseReference newScoreRef = mDatabase.child(Constants.getFirebaseLocationUsers(mContext)).child(mUserId).child(Constants.getFirebaseLocationScores(mContext)).push();
        Score newScore = new Score(change, score);
        newScoreRef.setValue(newScore);
    }

    private CountDownTimer initTimer(long totalMs, long tickIntervalMs) {
        return new CountDownTimer(totalMs, tickIntervalMs) {
            @Override
            public void onTick(long msTillFinished) {
                // Save how many seconds are remaining in case we need to restart with a new timer when the user pauses
                millisRemaining = msTillFinished;
                int progress = (int) (msTillFinished / 1000);
                mPbPractice.setProgress(mPbPractice.getMax() - progress);
                mTvTimeRemaining.setText(String.valueOf(progress));
            }

            @Override
            public void onFinish() {
                millisRemaining = 0;
                toggleFab(mBtnPause);
                toggleFab(mBtnPlay);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        final MediaPlayer mp = MediaPlayer.create(mContext, notification);
                        mp.start();

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
                                addScore(scorePicker.getValue());
                                mp.stop();
                            }
                        });
                        builder.setNegativeButton(R.string.dialogue_times_up_cancel_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                mp.stop();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
        };
    }

    private void startPlay(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        int countdownMs = Integer.valueOf(sharedPref.getString(mCountdownTimeKey, "" + (mCountdownMs / 1000))) * 1000;
        int leadinMs = Integer.valueOf(sharedPref.getString(mLeadinTimeKey, "" + (mLeadinMs / 1000))) * 1000;
        int totalMs = countdownMs + leadinMs;
        int totalSeconds = totalMs / 1000;

        // Decide if we're resuming a timer or starting a new one
        long useMillis = millisRemaining <= 0 ? totalMs : millisRemaining;

        mPbPractice.setMax(totalSeconds);
        // Start the timer
        mPracticeTimer = initTimer(useMillis, mCountdownIntervalMs);
        mPracticeTimer.start();
    }
}