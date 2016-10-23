package io.coderunner.chordmaster.data.model;

import com.google.firebase.database.ServerValue;

import org.parceler.Parcel;
import org.parceler.Transient;

@Parcel
public class Score {

    public Change change;
    public int score;
    @Transient
    public Object time;
    public long timestamp;

    public Score() {
    }

    public Score(Change change, int score) {
        this.change = change;
        this.score = score;
        this.time = ServerValue.TIMESTAMP;
    }

    public Long getCreatedTimestamp() {
        if (time instanceof Long) {
            return (Long) time;
        } else {
            return null;
        }
    }

    public void setCreatedTimestamp(long timestamp){
        this.timestamp = timestamp;
    }

    public Change getChange() {
        return change;
    }

    public int getScore() {
        return score;
    }

    public Object getTime() {
        return time;
    }

    public String getChangeString() {
        return change.getChangeString();
    }
}