package io.coderunner.chordmaster.util;

import android.content.Context;

import io.coderunner.chordmaster.R;

public class Constants {

    public static final int LOADER_ID_FRAG_WELCOME = 0;
    public static final int LOADER_ID_FRAG_CHORDS = 1;

    public static final String CHORD1_BUNDLE_KEY = "CHORD1";
    public static final String CHORD2_BUNDLE_KEY = "CHORD2";
    public static final String TIME_REMAINING_KEY = "TIMER";

    // Firebase
    public static String getFirebaseLocationUsers(Context context) {
        return context.getString(R.string.firebase_path_users);
    }

    public static String getFirebaseLocationScores(Context context) {
        return context.getString(R.string.firebase_path_scores);
    }

}
