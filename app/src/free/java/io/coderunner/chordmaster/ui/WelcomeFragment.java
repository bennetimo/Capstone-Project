package io.coderunner.chordmaster.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.coderunner.chordmaster.R;

public class WelcomeFragment extends Fragment {

    @BindView(R.id.btnQuickChord) Button mBtnQuickChord;
    @BindView(R.id.btnChooseChord) Button mBtnChooseChord;
    @BindView(R.id.adView) AdView mAdView;
    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        MobileAds.initialize(mContext, getString(R.string.firebase_ads_app_id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_welcome, container, false);
        ButterKnife.bind(this, root);

        // Render the banner ad
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("3D7A81A8C6EFE58743D4EA139908457B")
                .build();
        mAdView.loadAd(adRequest);

        return root;
    }
}
