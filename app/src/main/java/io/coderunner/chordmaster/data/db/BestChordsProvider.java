package io.coderunner.chordmaster.data.db;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(
        authority = BestChordsProvider.AUTHORITY,
        database = BestChordsDatabase.class,
        packageName = "io.coderunner.chordmaster.provider")
public final class BestChordsProvider {

    public static final String AUTHORITY = "io.coderunner.chordmaster.provider.BestChordsProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = BestChordsDatabase.BEST)
    public static class Best {

        @ContentUri(
                path = "best",
                type = "vnd.android.cursor.dir/best",
                defaultSort = BestChordsColumns.SCORE + " ASC")
        public static final Uri BEST = Uri.parse("content://" + AUTHORITY + "/best");

        @InexactContentUri(
                path = "best/#",
                name = "LIST_ID",
                type = "vnd.android.cursor.item/best",
                whereColumn = BestChordsColumns._ID,
                pathSegment = 1)
        public static Uri withChords(String chord1, String chord2) {
            return buildUri("best", "chord1", chord1, "chord2", chord2);
        }
    }
}