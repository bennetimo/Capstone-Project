package io.coderunner.chordmaster.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aigestudio.wheelpicker.WheelPicker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.coderunner.chordmaster.R;
import io.coderunner.chordmaster.data.db.ChordsColumns;
import io.coderunner.chordmaster.data.db.ChordsProvider;

public class WelcomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    @BindView(R.id.chord1) WheelPicker picker;
    @BindView(R.id.chord2) WheelPicker picker2;
    @BindView(R.id.btnChooseChord) Button mBtnChooseChord;
    @BindView(R.id.adView) AdView mAdView;
    private Context mContext;

    private ArrayList<String> chords = new ArrayList<>();

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

        getLoaderManager().initLoader(0, null, this);

        ArrayList<String> choices = new ArrayList<>(1);
        choices.add(" ");

        picker.setAtmospheric(true);
        picker.setData(choices); //Avoid the sample data being displayed
        picker.setCurved(true);
        picker.setVisibleItemCount(5);
        picker.setCyclic(true);
        picker.setSelectedItemTextColor(R.color.lightBlueA400);

        picker2.setAtmospheric(true);
        picker2.setData(choices); //Avoid the sample data being displayed
        picker2.setCurved(true);
        picker2.setVisibleItemCount(5);
        picker2.setCyclic(true);
        picker2.setSelectedItemTextColor(R.color.lightBlueA400);

        mBtnChooseChord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int chord1 = picker.getCurrentItemPosition();
                int chord2 = picker2.getCurrentItemPosition();
                String c1 = chords.get(chord1);
                String c2 = chords.get(chord2);
                Intent intent = new Intent(mContext, PracticeActivity.class).putExtra(Intent.EXTRA_TEXT, c1 + "/" + c2);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity().getBaseContext(), ChordsProvider.Chords.CHORDS_URI,
                new String[]{ChordsColumns._ID, ChordsColumns.NAME}, null, null, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        chords.clear();
        while(data.moveToNext()) {
            chords.add(data.getString(data.getColumnIndex(ChordsColumns.NAME)));
        }
        picker.setData(chords);
        picker2.setData(chords);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    }
}
