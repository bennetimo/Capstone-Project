package io.coderunner.chordmaster.data.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.Table;

import java.util.ArrayList;
import java.util.Arrays;

import io.coderunner.chordmaster.ui.MainActivity;

@Database(
        version = ChordsDatabase.VERSION,
        packageName = "io.coderunner.chordmaster.provider")
public final class ChordsDatabase {

    public static final int VERSION = 1;

    @Table(ChordsColumns.class)
    public static final String CHORDS = "chords";

    @OnCreate
    /**
     * When the database is first created after the user installs the app, preload some common chords
     */
    public static void populateInitialChords(SQLiteDatabase db) {
        ArrayList<String> chords = new ArrayList<>(Arrays.asList(MainActivity.PRELOADED_CHORDS));

        for (String chord : chords) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ChordsColumns.NAME, chord);
            db.insert(CHORDS, null, contentValues);
        }
    }
}