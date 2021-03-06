package io.coderunner.chordmaster.ui;

import android.content.Context;
import android.content.DialogInterface;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.coderunner.chordmaster.R;
import io.coderunner.chordmaster.data.model.Change;
import io.coderunner.chordmaster.data.model.Score;
import io.coderunner.chordmaster.ui.ads.AdHolder;
import io.coderunner.chordmaster.ui.ads.FlavourAdHolder;
import io.coderunner.chordmaster.util.Constants;

import static io.coderunner.chordmaster.util.Constants.CHORD_CHANGE_KEY;
import static io.coderunner.chordmaster.util.Constants.PAUSED_KEY;
import static io.coderunner.chordmaster.util.Constants.TIME_REMAINING_KEY;

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
    @BindView(R.id.btnPlay)
    FloatingActionButton mBtnPlay;
    @BindInt(R.integer.default_countdown_seconds)
    int mDefaultCountdownSeconds;
    @BindInt(R.integer.default_leadin_seconds)
    int mDefaultLeadinSeconds;
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

    private Change change;

    private long millisRemaining;
    private int COUNTDOWN_MS;
    private int LEADIN_MS;

    private FirebaseUserProvider mCallback;
    private AdHolder mAdHolder;

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
        mAdHolder = new FlavourAdHolder(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_play, container, false);
        ButterKnife.bind(this, root);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

        // Extra box/unbox because preferences are always stored as strings, even if they are type number...
        // http://stackoverflow.com/questions/17844511/android-preferences-error-string-cannot-be-cast-to-int
        COUNTDOWN_MS = Integer.valueOf(sharedPref.getString(mCountdownTimeKey, String.valueOf(mDefaultCountdownSeconds))) * 1000;
        LEADIN_MS = Integer.valueOf(sharedPref.getString(mLeadinTimeKey, String.valueOf(mDefaultLeadinSeconds))) * 1000;

        // If we are resuming, continue the timer where we left off
        if(savedInstanceState != null){
            millisRemaining = savedInstanceState.getLong(TIME_REMAINING_KEY);
            change = Parcels.unwrap(savedInstanceState.getParcelable(CHORD_CHANGE_KEY));

            boolean wasPaused = savedInstanceState.getBoolean(PAUSED_KEY);
            if(!wasPaused) {
                Log.d(LOG_TAG, "Resuming timer with " + millisRemaining + "ms remaining");
                startPractice();
            } else {
                toggleFab(mBtnPause); //Disable pause if we're starting from fresh
                resetPractice();
            }

        } else {
            toggleFab(mBtnPause); //Disable pause if we're starting from fresh
            resetPractice();
        }

        mBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPracticeTimer.cancel();
                resetPractice();
                toggleFab(mBtnPause);
                toggleFab(mBtnPlay);
            }
        });

        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPractice();
            }
        });

        displayChordChange();
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPracticeTimer != null) {
            Log.d(LOG_TAG, "Cancelling timer as fragment is paused");
            mPracticeTimer.cancel();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(millisRemaining > 0){
            //The timer was in progress, add it to the bundle so that we can restart it when we resume
            outState.putLong(TIME_REMAINING_KEY, millisRemaining);
            Log.d(LOG_TAG, "Saving timer to bundle with " + millisRemaining + "ms remaining");
        }
        if(change != null){
            outState.putParcelable(CHORD_CHANGE_KEY, Parcels.wrap(change));
        }
        outState.putBoolean(PAUSED_KEY, !mBtnPause.isEnabled());
    }

    private void toggleFab(FloatingActionButton fab) {
        if (fab.isEnabled()) {
            // Disable it
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.background)));
            fab.setEnabled(false);
        } else {
            // Enable it
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightBlueA400)));
            fab.setEnabled(true);
        }
    }

    public void chordChange(Change c) {
        if (c != null) {
            change = c;
            displayChordChange();
        }
    }

    private void displayChordChange(){
        if(mTvChordChange != null && change != null){
            mTvChordChange.setText(change.getChangeString());
            mTvChordChange.setContentDescription(change.getChord1().getName() + " to " + change.getChord2().getName());
        }
    }

    public void addScore(int score) {
        DatabaseReference newScoreRef = mDatabase.child(Constants.getFirebaseLocationUsers(mContext)).child(mCallback.getFirebaseUser()).child(Constants.getFirebaseLocationScores(mContext)).push();
        Score newScore = new Score(change, score);
        newScoreRef.setValue(newScore);
    }

    private long resetPractice(){
        int totalMs = COUNTDOWN_MS + LEADIN_MS;
        int totalSeconds = totalMs / 1000;

        // Decide if we're resuming a timer or starting a new one
        long useMillis = millisRemaining <= 0 ? totalMs : millisRemaining;
        mPbPractice.setMax(totalSeconds);

        int progress = (int) (useMillis / 1000);
        mPbPractice.setProgress(mPbPractice.getMax() - (mPbPractice.getMax() - progress));
        mTvTimeRemaining.setText(String.valueOf(useMillis / 1000));

        return useMillis;
    }

    private CountDownTimer initTimer(long totalMs, long tickIntervalMs) {
        return new CountDownTimer(totalMs, tickIntervalMs) {
            @Override
            public void onTick(long msTillFinished) {
                // Save how many seconds are remaining in case we need to restart with a new timer when the user pauses
                millisRemaining = msTillFinished;
                int progress = (int) (msTillFinished / 1000);
                mPbPractice.setProgress(mPbPractice.getMax() - (mPbPractice.getMax() - progress));
                mTvTimeRemaining.setText(String.valueOf(progress));
            }

            @Override
            public void onFinish() {
                millisRemaining = 0;
                toggleFab(mBtnPause);
                toggleFab(mBtnPlay);
                resetPractice();

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
                                nextPlay();
                            }
                        });
                        builder.setNegativeButton(R.string.dialogue_times_up_cancel_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mp.stop();
                                nextPlay();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
        };
    }

    private void nextPlay(){
        mAdHolder.showAdIfAvailable();
    }

    // When we've just started the timer, pause should be enabled, play disabled
    public void initFabs(){
        if(!mBtnPause.isEnabled())
            toggleFab(mBtnPause);
        if(mBtnPlay.isEnabled())
            toggleFab(mBtnPlay);
    }

    private void startPractice() {
        long ms = resetPractice();
        // Start the timer
        mPracticeTimer = initTimer(ms, mCountdownIntervalMs);
        mPracticeTimer.start();
        initFabs();
    }
}
