package io.coderunner.chordmaster.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.firebase.database.ServerValue;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Score {

    public String change;
    public int score;
    @JsonProperty
    public Object time;

    public Score(){}

    public Score(String change, int score) {
        this.change = change;
        this.score = score;
        this.time = ServerValue.TIMESTAMP;
    }

    @JsonIgnore
    public Long getCreatedTimestamp() {
        if (time instanceof Long) {
            return (Long) time;
        }
        else {
            return null;
        }
    }
}