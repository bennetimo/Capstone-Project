package io.coderunner.chordmaster.ui.ads;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import io.coderunner.chordmaster.R;

/**
 * Displays real ads when using free flavour
 */
public class FlavourAdHolder implements AdHolder {

    private Context mContext;
    private InterstitialAd mInterstitialAd;

    public FlavourAdHolder(Context context) {
        this.mContext = context;
        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId(mContext.getString(R.string.firebase_ads_interstitial_post_practice));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(mContext.getString(R.string.test_device_id))
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public void showAdIfAvailable(){
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }
}
