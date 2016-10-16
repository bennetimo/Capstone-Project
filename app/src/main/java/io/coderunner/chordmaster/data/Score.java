package io.coderunner.chordmaster.data;

public class Score {

    public String change;
    public int score;
    public long time;

    public Score(){}

    public Score(String change, int score, long time) {
        this.change = change;
        this.score = score;
        this.time = time;
    }
}
