package com.example.macarus0.walkiewalkie.util;

import android.util.Log;

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
         } catch (ParseException p ) {
             Log.e(TAG, "getStringDate: Unable to parse date " + timestamp);
             dateString = timestamp;
         }
         return dateString;
    }

    public static long calculateDuration(String timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat(timestampFormatString);
        long timeDelta;
        try {
            Date d = sdf.parse(timestamp);
            Date now = new Date();
            timeDelta = now.getTime() - d.getTime();
        } catch (ParseException p ) {
            Log.e(TAG, "calculateDuration: Unable to parse date " + timestamp);
            timeDelta = 0;
        }
        return timeDelta;
    }

    public static String getDurationString(long time) {

        return "0:42";
    }

}
