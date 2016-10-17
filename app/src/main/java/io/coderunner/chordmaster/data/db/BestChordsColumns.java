package io.coderunner.chordmaster.data.db;


import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface BestChordsColumns {

    @DataType(INTEGER) @PrimaryKey @AutoIncrement
    String _ID = "_id";

    @DataType(TEXT) @NotNull
    String CHORD1 = "chord1";

    @DataType(TEXT) @NotNull
    String CHORD2 = "chord2";

    @DataType(INTEGER) @NotNull
    String SCORE = "score";
}
