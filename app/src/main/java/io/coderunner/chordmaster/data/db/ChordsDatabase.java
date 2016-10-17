package io.coderunner.chordmaster.data.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.Table;

@Database(
        version = ChordsDatabase.VERSION,
        packageName = "io.coderunner.chordmaster.provider")
public final class ChordsDatabase {

    private static final String[] chordNames = {
        "E", "F", "C", "Am"
    };
    private static final String[] chordTypes = {
            "Major", "Major", "Major", "Minor"
    };

    public static final int VERSION = 1;

    @Table(ChordsColumns.class) public static final String CHORDS = "chords";

    @OnCreate
    public static void populateInitialChords(SQLiteDatabase db){
        for(int i=0; i<chordNames.length; i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(ChordsColumns.NAME, chordNames[i]);
            contentValues.put(ChordsColumns.TYPE, chordTypes[i]);
            db.insert(CHORDS, null, contentValues);
        }
    }
}