package com.example.macarus0.walkiewalkie.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.macarus0.walkiewalkie.R;
import com.example.macarus0.walkiewalkie.data.Owner;
import com.example.macarus0.walkiewalkie.data.Walk;
import com.example.macarus0.walkiewalkie.data.WalkLocation;
import com.example.macarus0.walkiewalkie.data.WalkPhoto;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class WalkEmailUtil {

    Walk mWalk;
    List<WalkLocation> mWalkLocations;
    List<WalkPhoto> mWalkPhotos;
    List<Owner> mWalkOwners;
    Context mContext;

    public WalkEmailUtil(Context context, Walk walk, List<WalkLocation> walkLocations,
                         List<WalkPhoto> walkPhotos, List<Owner> walkOwners) {
        this.mContext = context;
        this.mWalk = walk;
        this.mWalkLocations = walkLocations;
        this.mWalkPhotos = walkPhotos;
        this.mWalkOwners = walkOwners;
    }
    public Intent getEmailIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("text/plain");
        getEmailAddresses();
        intent.putExtra(Intent.EXTRA_EMAIL, getEmailAddresses());
        intent.putExtra(Intent.EXTRA_SUBJECT, getEmailSubject());
        intent.putExtra(Intent.EXTRA_HTML_TEXT, getEmailHTML());
        intent.putExtra(Intent.EXTRA_TEXT, getEmailText());
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, getImageUris());
        Log.i(TAG, "getEmailIntent: "+ getEmailHTML());
        return intent;
    }

    public ArrayList<Uri> getImageUris() {
        ArrayList<Uri> photoUris = new ArrayList<Uri>();
        for(WalkPhoto walkPhoto: mWalkPhotos) {
            photoUris.add(Uri.parse(walkPhoto.getPhotoUri()));
        }
        return photoUris;
    }

    private String[] getEmailAddresses() {
        String[] emails = new String[mWalkOwners.size()];
        int i = 0;
        for(Owner owner: mWalkOwners) {
            emails[i++] = owner.getEmailAddress();
        }
        Log.i(TAG, "getEmailAddresses: "+ emails);
        return emails;
    }

    public String getEmailSubject() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(mContext.getString(R.string.email_subject),
                TimeStampUtil.getStringDate(mWalk.getWalkDate())));
        return sb.toString();
    }

    public String getEmailText() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(mContext.getString(R.string.email_text_duration_distance),
                mWalk.getWalkDuration(), this.getWalkDistance()));
        sb.append(String.format("\n\n%s: %s", mContext.getString(R.string.email_map_label), mWalk.getWalkPathUrl() ));

        return sb.toString();
    }

    public String getEmailHTML() {
        StringBuilder sb = new StringBuilder();
        String distanceString = String.format(mContext.getString(R.string.email_text_duration_distance),
                mWalk.getWalkDuration(), this.getWalkDistance());
        sb.append(String.format("<p>%s</p>", distanceString));
        sb.append(String.format("<p><a href=\"%s\">%s.</a></p>", mWalk.getWalkPathUrl(), mContext.getString(R.string.email_map_label)));
        return sb.toString();
    }

    private String getWalkDistance() {
        return String.format(mContext.getString(R.string.email_text_distance_km), mWalk.getWalkDistance());
    }


}
