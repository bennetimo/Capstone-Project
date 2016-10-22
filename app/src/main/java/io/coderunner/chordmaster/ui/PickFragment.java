package io.coderunner.chordmaster.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aigestudio.wheelpicker.WheelPicker;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.coderunner.chordmaster.R;
import io.coderunner.chordmaster.data.db.ChordsColumns;
import io.coderunner.chordmaster.data.db.ChordsProvider;
import io.coderunner.chordmaster.data.model.Change;
import io.coderunner.chordmaster.data.model.Chord;
import io.coderunner.chordmaster.task.RandomChangeTask;

import static io.coderunner.chordmaster.util.Constants.CHORD1_BUNDLE_KEY;
import static io.coderunner.chordmaster.util.Constants.CHORD2_BUNDLE_KEY;
import static io.coderunner.chordmaster.util.Constants.LOADER_ID_FRAG_WELCOME;

public class PickFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.chord1)
    WheelPicker chord1Picker;
    @BindView(R.id.chord2)
    WheelPicker chord2Picker;
    @BindView(R.id.btnRandomChord)
    FloatingActionButton mBtnRandomChord;
    private Context mContext;

    private ChordChangeListener mCallback;


    // Container Activity must implement this interface
    public interface ChordChangeListener {
        void onChordChange(Change change);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (ChordChangeListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement ChordChangeListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pick, container, false);
        ButterKnife.bind(this, root);

        getLoaderManager().initLoader(LOADER_ID_FRAG_WELCOME, null, this);

        initWheelPicker(chord1Picker);
        initWheelPicker(chord2Picker);

        // If we have saved the chosen chords, reload them
        if(savedInstanceState != null){
            int savedChord1 = savedInstanceState.getInt(CHORD1_BUNDLE_KEY);
            int savedChord2 = savedInstanceState.getInt(CHORD2_BUNDLE_KEY);
            chord1Picker.setSelectedItemPosition(savedChord1);
            chord2Picker.setSelectedItemPosition(savedChord2);
        }

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
        picker.setVisibleItemCount(getResources().getInteger(R.integer.chord_picker_num_visible));
        picker.setCyclic(true);
        picker.setSelectedItemTextColor(R.color.lightBlueA400);
        picker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                mCallback.onChordChange(getSelectedChange());
            }
        });
    }

    public Change getSelectedChange() {
        Chord chord1 = (Chord) chord1Picker.getData().get(chord1Picker.getCurrentItemPosition());
        Chord chord2 = (Chord) chord2Picker.getData().get(chord2Picker.getCurrentItemPosition());
        return new Change(chord1, chord2);
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
            // Trigger initial chord change
            mCallback.onChordChange(getSelectedChange());
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(chord1Picker != null && chord2Picker != null) {
            // Save the state of the chord pickers
            outState.putInt(CHORD1_BUNDLE_KEY, chord1Picker.getCurrentItemPosition());
            outState.putInt(CHORD2_BUNDLE_KEY, chord2Picker.getCurrentItemPosition());
        }
    }

}