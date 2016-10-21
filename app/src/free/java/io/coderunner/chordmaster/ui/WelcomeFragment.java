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

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.coderunner.chordmaster.R;
import io.coderunner.chordmaster.data.db.ChordsColumns;
import io.coderunner.chordmaster.data.db.ChordsProvider;
import io.coderunner.chordmaster.data.model.Change;
import io.coderunner.chordmaster.data.model.Chord;
import io.coderunner.chordmaster.task.RandomChangeTask;

import static io.coderunner.chordmaster.util.Constants.LOADER_ID_FRAG_WELCOME;

public class WelcomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.chord1)
    WheelPicker chord1Picker;
    @BindView(R.id.chord2)
    WheelPicker chord2Picker;
    @BindView(R.id.btnChooseChord)
    Button mBtnChooseChord;
    @BindView(R.id.btnRandomChord)
    Button mBtnRandomChord;
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

        initWheelPicker(chord1Picker);
        initWheelPicker(chord2Picker);

        mBtnChooseChord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Chord chord1 = (Chord) chord1Picker.getData().get(chord1Picker.getCurrentItemPosition());
                Chord chord2 = (Chord) chord2Picker.getData().get(chord2Picker.getCurrentItemPosition());
                Change change = new Change(chord1, chord2);

                Intent intent = new Intent(mContext, PracticeActivity.class).putExtra(Intent.EXTRA_TEXT, Parcels.wrap(change));
                startActivity(intent);
            }
        });

        mBtnRandomChord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBtnRandomChord.setEnabled(false);
                RandomChangeTask task = new RandomChangeTask(chord1Picker, chord2Picker, mBtnRandomChord);
                task.execute();
            }
        });

        return root;
    }

    private void initWheelPicker(WheelPicker picker) {
        picker.setAtmospheric(true);
        picker.setCurved(true);
        picker.setVisibleItemCount(7);
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
            ArrayList<Chord> swap = new ArrayList<>();
            data.moveToFirst();
            while (data.moveToNext()) {
                String chordName = data.getString(data.getColumnIndex(ChordsColumns.NAME));
                Chord chord = new Chord(chordName);
                swap.add(chord);
            }
            chord1Picker.setData(swap);
            chord2Picker.setData(swap);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    }
}
