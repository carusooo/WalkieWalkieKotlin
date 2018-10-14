package com.example.macarus0.walkiewalkie.util;

import android.content.Context;
import android.util.Log;

import com.example.macarus0.walkiewalkie.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeStampUtil {
    private static String TAG = "TimeStampUtil";
    private static String timestampFormatString = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static String dateFormatString = "dd MMM yyyy";

    public static String getStringTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat(timestampFormatString);
        return sdf.format(new Date());
    }

    public static String getStringDate(String timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat(timestampFormatString);
        String dateString;
        try {
            Date d = sdf.parse(timestamp);
            sdf.applyPattern(dateFormatString);
            dateString = sdf.format(d);
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

    public static long calculateDuration(String startTimeStamp, String endTimeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat(timestampFormatString);
        long timeDelta;
        try {
            Date start = sdf.parse(startTimeStamp);
            Date end = sdf.parse(endTimeStamp);
            timeDelta = end.getTime() - start.getTime();
        } catch (ParseException p) {
            Log.e(TAG, "calculateDuration: Unable to parse date " + startTimeStamp);
            timeDelta = 0;
        }
        return timeDelta;
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
