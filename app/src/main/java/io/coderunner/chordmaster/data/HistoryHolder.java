package io.coderunner.chordmaster.data;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.coderunner.chordmaster.R;

public class HistoryHolder extends RecyclerView.ViewHolder {
    private View mView;

    private DateFormat formatter = new SimpleDateFormat("MMM dd HH:mm");

    public HistoryHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setChordpair(String name) {
        TextView field = (TextView) mView.findViewById(R.id.tv_history_chordpair);
        field.setText(name);
    }

    public void setScore(int score) {
        TextView field = (TextView) mView.findViewById(R.id.tv_history_score);
        field.setText("" + score);
    }

    public void setAchieved(long time) {
        Date date = new Date(time);

        TextView field = (TextView) mView.findViewById(R.id.tv_history_achieved);
        field.setText(formatter.format(date));
    }
}
