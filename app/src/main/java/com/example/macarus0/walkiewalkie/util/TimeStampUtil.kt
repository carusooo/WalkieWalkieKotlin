package com.example.macarus0.walkiewalkie.util

import android.content.Context
import android.util.Log
import com.example.macarus0.walkiewalkie.R
import java.text.DateFormat.getDateInstance
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object TimeStampUtil {
    private const val TAG = "TimeStampUtil"
    private const val timestampFormatString = "yyyy-MM-dd'T'HH:mm:ssZ"

    // Returns a
    val stringTimestamp: String
        get() = Date().toString()

    val time: Long
        get() {
            val date = Date()
            return date.time
        }

    fun getStringDate(timestamp: String): String {
        val sdf = SimpleDateFormat(timestampFormatString, Locale.US)
        var dateString: String
        try {
            val d = sdf.parse(timestamp)
            dateString = getDateInstance().format(d)
        } catch (p: ParseException) {
            Log.e(TAG, "getStringDate: Unable to parse date $timestamp")
            dateString = timestamp
        }

        return dateString
    }

    fun getDurationString(context: Context, timeDelta: Long): String {
        val durationString = StringBuilder()
        val timeDeltaSeconds = timeDelta / 1000
        val timeDeltaMinutes = timeDeltaSeconds / 60
        val timeDeltaHours = timeDeltaMinutes / 60
        if (timeDeltaHours > 0) {
            durationString.append(String.format(context.resources.getString(R.string.hours_format), timeDeltaHours))
        }
        if (timeDeltaMinutes > 0) {
            durationString.append(String.format(context.resources.getString(R.string.minutes_format), timeDeltaMinutes % 60))
        }
        durationString.append(String.format(context.resources.getString(R.string.seconds_format), timeDelta % 60))
        return durationString.toString()
    }
}
