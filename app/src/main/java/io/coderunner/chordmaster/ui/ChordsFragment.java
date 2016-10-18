package io.coderunner.chordmaster.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.coderunner.chordmaster.R;
import io.coderunner.chordmaster.data.ChordsCursorAdapter;
import io.coderunner.chordmaster.data.db.ChordsColumns;
import io.coderunner.chordmaster.data.db.ChordsProvider;

public class ChordsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.fab_add_chord) FloatingActionButton mFabAddChord;
    @BindView(R.id.recyclerview_chords)
    RecyclerView mRecyclerViewChords;
    private Context mContext;

    private static final int LOADER_ID = 0;

    private ChordsCursorAdapter mCursorAdapter;
    private Cursor mCursor;

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
        getLoaderManager().initLoader(LOADER_ID, null, this);
        mRecyclerViewChords.setAdapter(mCursorAdapter);

        mFabAddChord.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                new MaterialDialog.Builder(mContext).title(R.string.dialogue_add_chord_title)
                    .content(R.string.dialogue_add_chord_content)
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input(R.string.dialogue_add_chord_hint, R.string.dialogue_add_chord_prefill, new MaterialDialog.InputCallback() {
                        @Override public void onInput(MaterialDialog dialog, CharSequence input) {
                            // Check if the chord exists, if it doesn't, add it
                            Cursor c = getActivity().getContentResolver().query(ChordsProvider.Chords.CHORDS_URI,
                                    new String[] { ChordsColumns.NAME }, ChordsColumns.NAME + "= ?",
                                    new String[] { input.toString() }, null);
                            if (c.getCount() != 0) {
                                Toast toast =
                                        Toast.makeText(getActivity(), getActivity().getString(R.string.dialogue_error_chord_exists),
                                                Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0);
                                toast.show();
                                return;
                            } else {
                                // Add the chord to the DB
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(ChordsColumns.NAME, input.toString());
                                contentValues.put(ChordsColumns.TYPE, "Major");
                                getActivity().getContentResolver().insert(ChordsProvider.Chords.CHORDS_URI, contentValues);
                            }
                        }
                    })
                    .show();

            }
        });
        return root;
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity().getBaseContext(), ChordsProvider.Chords.CHORDS_URI,
                new String[]{ChordsColumns._ID, ChordsColumns.NAME, ChordsColumns.TYPE}, null, null, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
        mCursor = data;
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

}
