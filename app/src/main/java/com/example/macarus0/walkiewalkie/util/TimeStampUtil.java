package com.example.macarus0.walkiewalkie.util;

import android.content.Context;
import android.util.Log;

import com.example.macarus0.walkiewalkie.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.text.DateFormat.getDateInstance;

public class TimeStampUtil {
    private static String TAG = "TimeStampUtil";
    private static String timestampFormatString = "yyyy-MM-dd'T'HH:mm:ssZ";

    public static String getStringTimestamp() {
        // Returns a
        return new Date().toString();
    }

    public static String getStringDate(String timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat(timestampFormatString, Locale.US);
        String dateString;
        try {
            Date d = sdf.parse(timestamp);
            dateString = getDateInstance().format(d);
        } catch (ParseException p) {
            Log.e(TAG, "getStringDate: Unable to parse date " + timestamp);
            dateString = timestamp;
        }
        return dateString;
    }

    public static long getTime() {
        Date date = new Date();
        return date.getTime();
    }

    public static String getDurationString(Context context, long timeDelta) {
        StringBuilder durationString = new StringBuilder();
        long timeDeltaSeconds = timeDelta / 1000;
        long timeDeltaMinutes = timeDeltaSeconds / 60;
        long timeDeltaHours = timeDeltaMinutes / 60;
        if (timeDeltaHours > 0) {
            durationString.append(String.format(context.getResources().getString(R.string.hours_format), timeDeltaHours));
        }
        if (timeDeltaMinutes > 0) {
            durationString.append(String.format(context.getResources().getString(R.string.minutes_format), timeDeltaMinutes % 60));
        }
        durationString.append(String.format(context.getResources().getString(R.string.seconds_format), timeDelta % 60));
        return durationString.toString();
    }
}
