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
import io.coderunner.chordmaster.data.db.BestChordsColumns;

/**
 *  Credit to skyfishjy gist:
 *    https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * for the code structure
 */
public class BestCursorAdapter extends CursorRecyclerViewAdapter<BestCursorAdapter.ViewHolder> {

    private static Context mContext;
    private static Typeface robotoLight;
    private boolean isPercent;
    public BestCursorAdapter(Context context, Cursor cursor){
        super(context, cursor);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        //robotoLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor){
        String chord1 = cursor.getString(cursor.getColumnIndex(BestChordsColumns.CHORD1));
        String chord2 = cursor.getString(cursor.getColumnIndex(BestChordsColumns.CHORD2));
        viewHolder.chordpair.setText(chord1 + "/" + chord2);
    }

    @Override public int getItemCount() {
        return super.getItemCount();
    }

    public int getBestID(int position) {
        Cursor c = getCursor();
        c.moveToPosition(position);
        return c.getInt(c.getColumnIndex(BestChordsColumns._ID));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        public final TextView chordpair;
        public ViewHolder(View itemView){
            super(itemView);
            chordpair = (TextView) itemView.findViewById(R.id.chordpair);
            //chordpair.setTypeface(robotoLight);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
