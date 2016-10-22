package io.coderunner.chordmaster.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.coderunner.chordmaster.R;
import io.coderunner.chordmaster.data.ChordsCursorAdapter;
import io.coderunner.chordmaster.data.db.ChordsColumns;
import io.coderunner.chordmaster.data.db.ChordsProvider;

import static io.coderunner.chordmaster.util.Constants.LOADER_ID_FRAG_CHORDS;

public class ChordsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.fab_add_chord)
    FloatingActionButton mFabAddChord;
    @BindView(R.id.recyclerview_chords)
    RecyclerView mRecyclerViewChords;
    private Context mContext;

    private final String LOG_TAG = this.getClass().getSimpleName();

    private ChordsCursorAdapter mCursorAdapter;

    private Pattern mPattern;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mCursorAdapter = new ChordsCursorAdapter(getActivity(), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chords, container, false);
        ButterKnife.bind(this, root);
        mRecyclerViewChords.setLayoutManager(new LinearLayoutManager(getActivity()));
        getLoaderManager().initLoader(LOADER_ID_FRAG_CHORDS, null, this);
        mRecyclerViewChords.setAdapter(mCursorAdapter);

        mPattern = Pattern.compile(getActivity().getString(R.string.input_regex_pattern));

        mFabAddChord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(mContext).title(R.string.dialogue_add_chord_title)
                        .content(R.string.dialogue_add_chord_content)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input(R.string.dialogue_add_chord_hint, R.string.dialogue_add_chord_prefill, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                            if(checkChordValid(input.toString())){
                                // Add the chord to the DB
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(ChordsColumns.NAME, input.toString());
                                getActivity().getContentResolver().insert(ChordsProvider.Chords.CHORDS_URI, contentValues);
                            }
                            }
                        })
                        .show();
            }
        });
        return root;
    }

    private boolean checkChordValid(String chordName){
        // Check if the chord name is valid
        Matcher matcher = mPattern.matcher(chordName);
        if(!matcher.matches()){
            toast(getActivity().getString(R.string.dialogue_error_chord_not_valid));
            Log.d(LOG_TAG, "Chord name input was invalid: " + chordName);
            return false;
        }
        // Check if the chord exists, if it doesn't, add it
        Cursor c = getActivity().getContentResolver().query(ChordsProvider.Chords.CHORDS_URI,
                new String[]{ChordsColumns.NAME}, ChordsColumns.NAME + "= ?",
                new String[]{chordName.toString()}, null);
        if (c.getCount() != 0) {
            toast(getActivity().getString(R.string.dialogue_error_chord_exists));
            Log.d(LOG_TAG, "Chord name input was invalid: " + chordName);
            return false;
        }
        return true;
    }

    private void toast(String message){
        Toast toast =
                Toast.makeText(getActivity(), message,
                        Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0);
        toast.show();
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity().getBaseContext(), ChordsProvider.Chords.CHORDS_URI,
                new String[]{ChordsColumns._ID, ChordsColumns.NAME}, null, null, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

}
