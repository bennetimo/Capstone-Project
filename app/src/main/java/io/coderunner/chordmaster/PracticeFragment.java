package io.coderunner.chordmaster;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PracticeFragment extends Fragment {

    private CountDownTimer mPracticeTimer;
    @BindView(R.id.pbPractice) ProgressBar mPbPractice;
    @BindView(R.id.btnStop) Button mBtnStop;
    @BindView(R.id.tvTimeRemaining) TextView mTvTimeRemaining;
    @BindInt(R.integer.countdown_ms) int mCountdownMs;
    @BindInt(R.integer.leadin_ms) int mLeadinMs;
    @BindInt(R.integer.countdown_interval_ms) int mCountdownIntervalMs;
    @BindInt(R.integer.score_picker_max_value) int mScorePickerMax;
    @BindInt(R.integer.score_picker_min_value) int mScorePickerMin;
    @BindInt(R.integer.score_picker_default_value) int mScorePickerDefault;
    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_practice, container, false);
        ButterKnife.bind(this, root);

        int totalMs = mCountdownMs + mLeadinMs;
        int totalSeconds = totalMs / 1000;

        mPbPractice.setMax(totalSeconds);

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


                        NumberPicker scorePicker = (NumberPicker) dialogue.findViewById(R.id.scorePicker);
                        scorePicker.setMaxValue(mScorePickerMax);
                        scorePicker.setMinValue(mScorePickerMin);
                        scorePicker.setValue(mScorePickerDefault);

                        builder.setView(dialogue);

                        builder.setMessage(R.string.dialogue_times_up_message)
                                .setTitle(R.string.dialogue_times_up_title);


                        // Add the buttons
                        builder.setPositiveButton(R.string.dialogue_times_up_ok_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
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
}