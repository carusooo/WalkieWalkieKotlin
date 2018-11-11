package com.example.macarus0.walkiewalkie.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.macarus0.walkiewalkie.R;
import com.example.macarus0.walkiewalkie.data.Walk;
import com.example.macarus0.walkiewalkie.data.WalkLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static android.support.constraint.Constraints.TAG;

public class WalkEmailUtil {

    private final String MAP_URL = "https://maps.googleapis.com/maps/api/staticmap?";
    private final String MAP_SIZE = "640x480";

    Walk mWalk;
    List<WalkLocation> mWalkLocations;
    Context mContext;
    public WalkEmailUtil(Context context, Walk walk, List<WalkLocation> walkLocations) {
        this.mContext = context;
        this.mWalk = walk;
        this.mWalkLocations = walkLocations;
    }
    public Intent getEmailIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_SUBJECT, getEmailSubject());
        intent.putExtra(Intent.EXTRA_HTML_TEXT, getEmailHTML());
        intent.putExtra(Intent.EXTRA_TEXT, getEmailText());
        Log.i(TAG, "getEmailIntent: "+ getEmailHTML());
        return intent;
    }

    public String getEmailSubject() {
        StringBuilder sb = new StringBuilder();
        sb.append(mContext.getString(R.string.email_subject));
        sb.append(TimeStampUtil.getStringDate(mWalk.getWalkDate()));
        return sb.toString();
    }

    public String getEmailText() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(mContext.getString(R.string.email_text_duration_distance),
                mWalk.getWalkDuration(), mWalk.getWalkDistance()));
        sb.append(String.format("\n\n%s: %s", mContext.getString(R.string.email_map_label), getMapURL() ));

        return sb.toString();
    }

    public String getEmailHTML() {
        StringBuilder sb = new StringBuilder();
        String distanceString = String.format(mContext.getString(R.string.email_text_duration_distance),
                mWalk.getWalkDuration(), mWalk.getWalkDistance());
        sb.append(String.format("<p>%s</p>", distanceString));
        sb.append(String.format("<p><a href=\"%s\">%s.</a></p>", getMapURL(), mContext.getString(R.string.email_map_label)));
        return sb.toString();
    }

    private String getMapURL() {
        StringBuilder sb = new StringBuilder();
        sb.append(MAP_URL);
        sb.append("path=");
        String result = mWalkLocations.stream().map(WalkLocation::toString)
                .collect(Collectors.joining("|"));
        sb.append(result);
        sb.append(String.format("&size=%s", MAP_SIZE));
        sb.append(String.format("&key=%s", "AIzaSyAalNWRPNCOrokAA48IR-blbRHwRS2txbA"));
        Log.i(TAG, "getMapURL: "+sb.toString());
        return sb.toString();
    }


}
