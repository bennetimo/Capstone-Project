package io.coderunner.chordmaster.data.db;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(
        authority = ChordsProvider.AUTHORITY,
        database = ChordsDatabase.class,
        packageName = "io.coderunner.chordmaster.provider")
public final class ChordsProvider {

    public static final String AUTHORITY = "io.coderunner.chordmaster.provider.ChordsProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = ChordsDatabase.CHORDS)
    public static class Chords {

        private static final String CHORDS_PATH = "chords";

        @ContentUri(
                path = CHORDS_PATH,
                type = "vnd.android.cursor.dir/" + CHORDS_PATH,
                defaultSort = ChordsColumns.NAME + " ASC")
        public static final Uri CHORDS_URI = buildUri(CHORDS_PATH);

    }
}