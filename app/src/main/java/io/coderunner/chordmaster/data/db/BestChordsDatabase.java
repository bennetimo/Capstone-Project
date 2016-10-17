package io.coderunner.chordmaster.data.db;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(
        version = BestChordsDatabase.VERSION,
        packageName = "io.coderunner.chordmaster.provider")
public final class BestChordsDatabase {

    public static final int VERSION = 1;

    @Table(BestChordsColumns.class) public static final String BEST = "best";
}