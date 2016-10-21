package io.coderunner.chordmaster.data.model;

import com.google.firebase.database.Exclude;

import org.parceler.Parcel;

@Parcel
public class Change {

    public Chord chord1;
    public Chord chord2;

    public Change() {
    }

    public Change(Chord chord1, Chord chord2) {
        this.chord1 = chord1;
        this.chord2 = chord2;
    }

    public Chord getChord1() {
        return chord1;
    }

    public Chord getChord2() {
        return chord2;
    }

    @Exclude
    public String getChangeString() {
        return chord1.getName() + "/" + chord2.getName();
    }

    @Exclude
    public String getContentDescriptionString() {
        return chord1.getName() + " to " + chord2.getName();
    }
}
