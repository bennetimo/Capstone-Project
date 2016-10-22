package io.coderunner.chordmaster.ui.ads;

import android.content.Context;

/**
 * Dummy implementation for paid variant, as there are no ads
 */
public class FlavourAdHolder implements AdHolder {

    public FlavourAdHolder(Context context) {}

    @Override
    public void showAdIfAvailable() {
        // Do nothing
    }
}