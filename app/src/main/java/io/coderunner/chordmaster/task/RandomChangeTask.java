package io.coderunner.chordmaster.task;

import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.view.MotionEvent;

import com.aigestudio.wheelpicker.WheelPicker;

import java.util.Random;

/**
 * Simulates a random fling on the chord spinner controls, to create an effect
 * like a slot machine
 */
public class RandomChangeTask extends AsyncTask<Void, Void, Void> {

    private final Random r = new Random();
    // How long the total touch event is simulated for
    private final int FLING_MS = 300;
    // How frequently to register an intermediate event. Lower numbers give smoother motion
    private final int TICK_INTERVAL_MS = 20;
    // How much randomness is involved. Higher number creates more randomness, and makes the two wheels diverge more
    private final int RANDOM_Y_MULTIPLIER = 80;
    // Higher makes the fling go much further
    private final int Y_MULTIPLIER = 5;
    private WheelPicker mChord1Picker;
    private WheelPicker mChord2Picker;
    private FloatingActionButton mRandomButton;

    public RandomChangeTask(WheelPicker mChord1Picker, WheelPicker mChord2Picker, FloatingActionButton mRandomButton) {
        this.mChord1Picker = mChord1Picker;
        this.mChord2Picker = mChord2Picker;
        this.mRandomButton = mRandomButton;
    }

    // Generate a motion event to simulate touch
    private MotionEvent motionEvent(float y, int action) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + TICK_INTERVAL_MS;
        return MotionEvent.obtain(
                downTime,
                eventTime,
                action,
                0.0f,
                y,
                0
        );
    }

    // Generate a motion event with a random y flick based on the given params
    private MotionEvent randomMotionEvent(long tick, int action) {
        float y = (r.nextFloat() * RANDOM_Y_MULTIPLIER) + (tick * Y_MULTIPLIER);
        return motionEvent(y, action);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        // Trigger a touch down event
        mChord1Picker.dispatchTouchEvent(motionEvent(0.0f, MotionEvent.ACTION_DOWN));
        mChord2Picker.dispatchTouchEvent(motionEvent(0.0f, MotionEvent.ACTION_DOWN));


        CountDownTimer timer = new CountDownTimer(FLING_MS, TICK_INTERVAL_MS) {
            @Override
            public void onTick(long l) {
                // Trigger touch move events
                mChord1Picker.dispatchTouchEvent(randomMotionEvent(l, MotionEvent.ACTION_MOVE));
                mChord2Picker.dispatchTouchEvent(randomMotionEvent(l, MotionEvent.ACTION_MOVE));
            }

            @Override
            public void onFinish() {
                // Trigger touch up events
                mChord1Picker.dispatchTouchEvent(randomMotionEvent(FLING_MS, MotionEvent.ACTION_UP));
                mChord2Picker.dispatchTouchEvent(randomMotionEvent(FLING_MS, MotionEvent.ACTION_UP));
                mRandomButton.setEnabled(true);
            }
        };
        timer.start();
    }
}
