package io.coderunner.chordmaster.util;

import android.content.Context;

import io.coderunner.chordmaster.R;

public class Constants {

    public static final int LOADER_ID_FRAG_WELCOME = 0;
    public static final int LOADER_ID_FRAG_CHORDS = 1;

    // Firebase
    public static String getFirebaseLocationUsers(Context context) {
        return context.getString(R.string.firebase_path_users);
    }

    public static String getFirebaseLocationScores(Context context) {
        return context.getString(R.string.firebase_path_scores);
    }

}
