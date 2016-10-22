package io.coderunner.chordmaster.util;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.coderunner.chordmaster.R;

public class Util {

    private static DateFormat formatter;

    public static DateFormat getDateFormatter(Context context) {
        if (formatter == null) {
            formatter = new SimpleDateFormat(context.getString(R.string.date_format_pattern), Locale.getDefault());
        }
        return formatter;
    }
}
