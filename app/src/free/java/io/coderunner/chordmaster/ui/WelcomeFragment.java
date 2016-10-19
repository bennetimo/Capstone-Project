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

import static io.coderunner.chordmaster.util.Constants.LOADER_ID_FRAG_WELCOME;

public class WelcomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.chord1)
    WheelPicker firstChordPicker;
    @BindView(R.id.chord2)
    WheelPicker secondChordPicker;
    @BindView(R.id.btnChooseChord)
    Button mBtnChooseChord;
    @BindView(R.id.adView)
    AdView mAdView;
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

        getLoaderManager().initLoader(LOADER_ID_FRAG_WELCOME, null, this);

        initWheelPicker(firstChordPicker);
        initWheelPicker(secondChordPicker);

        mBtnChooseChord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstChord = (String) firstChordPicker.getData().get(firstChordPicker.getCurrentItemPosition());
                String secondChord = (String) secondChordPicker.getData().get(secondChordPicker.getCurrentItemPosition());
                Intent intent = new Intent(mContext, PracticeActivity.class).putExtra(Intent.EXTRA_TEXT, firstChord + "/" + secondChord);
                startActivity(intent);
            }
        });

        return root;
    }

    private void initWheelPicker(WheelPicker picker) {
        picker.setAtmospheric(true);
        picker.setCurved(true);
        picker.setVisibleItemCount(5);
        picker.setCyclic(true);
        picker.setSelectedItemTextColor(R.color.lightBlueA400);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity().getBaseContext(), ChordsProvider.Chords.CHORDS_URI,
                new String[]{ChordsColumns._ID, ChordsColumns.NAME}, null, null, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        // Make sure that the wheel picker always has at least one element, to avoid a divide by zero
        // error when using cyclic mode at this line:
        // https://github.com/AigeStudio/WheelPicker/blob/110102b4834f919085d52143dd9440c7d77f2abe/WheelPicker/src/main/java/com/aigestudio/wheelpicker/WheelPicker.java#L533
        if (data.getCount() > 0) {
            ArrayList<String> swap = new ArrayList<>();
            data.moveToFirst();
            while (data.moveToNext()) {
                swap.add(data.getString(data.getColumnIndex(ChordsColumns.NAME)));
            }
            firstChordPicker.setData(swap);
            secondChordPicker.setData(swap);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    }
}
