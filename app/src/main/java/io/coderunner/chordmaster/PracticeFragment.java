package io.coderunner.chordmaster;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
                        Toast.makeText(mContext, getString(R.string.timer_expired), Toast.LENGTH_LONG).show();
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
