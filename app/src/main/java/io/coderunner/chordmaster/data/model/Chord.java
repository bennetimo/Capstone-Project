package io.coderunner.chordmaster.data.model;

import org.parceler.Parcel;

@Parcel
public class Chord {

    public String name;

    public Chord() {
    }

    public Chord(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
