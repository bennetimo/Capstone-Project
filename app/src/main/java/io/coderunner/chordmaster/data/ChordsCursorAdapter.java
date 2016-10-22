package io.coderunner.chordmaster.data;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.coderunner.chordmaster.R;
import io.coderunner.chordmaster.data.db.ChordsColumns;

/**
 * Credit to skyfishjy gist:
 * https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * for the code structure
 */
public class ChordsCursorAdapter extends CursorRecyclerViewAdapter<ChordsCursorAdapter.ViewHolder> {

    private static Context mContext;
    private static Typeface robotoLight;

    public ChordsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        robotoLight = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.chords_typeface));
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chord, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor) {
        String chordName = cursor.getString(cursor.getColumnIndex(ChordsColumns.NAME));
        viewHolder.chordName.setText(chordName);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public final TextView chordName;

        public ViewHolder(View itemView) {
            super(itemView);
            chordName = (TextView) itemView.findViewById(R.id.chordName);
            chordName.setTypeface(robotoLight);
            chordName.setContentDescription(chordName.getText());
        }

        @Override
        public void onClick(View v) {
        }
    }
}
