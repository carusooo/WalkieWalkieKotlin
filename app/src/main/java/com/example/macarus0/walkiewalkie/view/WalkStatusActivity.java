package com.example.macarus0.walkiewalkie.view;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.macarus0.walkiewalkie.R;
import com.example.macarus0.walkiewalkie.data.Walk;
import com.example.macarus0.walkiewalkie.data.WalkLocation;
import com.example.macarus0.walkiewalkie.util.LocationUtil;
import com.example.macarus0.walkiewalkie.util.PhotoReminderAlarm;
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.macarus0.walkiewalkie.util.TimeStampUtil.getDurationString;
import static com.example.macarus0.walkiewalkie.util.TimeStampUtil.getTime;

public class WalkStatusActivity extends AppCompatActivity {

    public static final String WALK_ID = "walk_id";
    public static final String WALK_TRACK_DISTANCE = "walk_track_distance";

    private static final String TAG = "WalkStatusActivity";
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL = 60000; // Every 60 seconds.
    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value, but they may be less frequent.
     */
    private static final long FASTEST_UPDATE_INTERVAL = 30000; // Every 30 seconds
    /**
     * The max time before batched results are delivered by location services. Results may be
     * delivered sooner than this interval.
     */
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 2; // Every 2 minutes.

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;


    @BindView(R.id.walk_distance_label)
    TextView mWalkDistanceLabel;
    @BindView(R.id.walk_distance_text)
    TextView mWalkDistanceText;
    @BindView(R.id.walk_end_walk_button)
    Button mEndWalkButton;
    @BindView(R.id.walk_skip_summary_button)
    Button mSkipSharingButton;
    private long mWalkId;
    private boolean mTrackDistance;
    private LocationRequest mLocationRequest;
    private Walk mWalk;
    private WalkieViewModel mWalkieViewModel;
    private WalkPhotosFragment mWalkPhotosFragment;
    private WalkDogsFragment mWalkDogsFragment;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationUtil locationUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_status);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mWalkId = intent.getLongExtra(WALK_ID, -1);
        mTrackDistance = intent.getBooleanExtra(WALK_TRACK_DISTANCE, false);
        Log.i(TAG, "onCreate: mTrack " + mTrackDistance);


        mWalkieViewModel = ViewModelProviders.of(this).get(WalkieViewModel.class);

        mEndWalkButton.setText(getString(R.string.end_walk));
        mEndWalkButton.setOnClickListener(view -> endWalk());
        mSkipSharingButton.setVisibility(View.GONE);

        mWalkDistanceLabel.setText(getString(R.string.walk_distance_label));

        if (mTrackDistance) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            startLocationTracking();
        }

        mWalkieViewModel.getWalkById(mWalkId).observe(this, this::showWalkUI);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (mWalkPhotosFragment == null) {
            mWalkPhotosFragment = WalkPhotosFragment.newInstance(true);
        }
        mWalkPhotosFragment.setWalkId(mWalkId);
        fragmentManager.beginTransaction()
                .replace(R.id.walk_photos, mWalkPhotosFragment)
                .commit();
        if (mWalkDogsFragment == null) {
            mWalkDogsFragment = WalkDogsFragment.newInstance();
        }
        mWalkDogsFragment.setWalkId(mWalkId);
        fragmentManager.beginTransaction()
                .replace(R.id.walk_dogs, mWalkDogsFragment)
                .commit();
    }

    private void showWalkUI(Walk walk) {
        mWalk = walk;
        mWalkDistanceText.setText(String.format("%.1f", mWalk.getWalkDistance()));
        Log.i(TAG, "showWalkUI: " + walk.getDogs());
    }

    private void startLocationTracking() {

        if(!checkPermissions()){
            requestPermissions();
        }
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        // Note: apps running on "O" devices (regardless of targetSdkVersion) may receive updates
        // less frequently than this interval when the app is no longer in the foreground.
        //mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setInterval(1000);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        //mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(1000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Sets the maximum time when batched location updates are delivered. Updates may be
        // delivered sooner than this interval.
        //mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);
        mLocationRequest.setMaxWaitTime(5000);

        locationUtil = new LocationUtil(this, mWalkId);

        try {
            Log.i(TAG, "Starting location tracking "+ mWalkId);
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationUtil, null);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        mWalkieViewModel.getLocations(mWalkId).observe(this, locations -> walkLocationUpdated(locations));
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.activity_walk_status),
                    R.string.location_request_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok,  view -> {
                            // Request permission
                            ActivityCompat.requestPermissions(WalkStatusActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(WalkStatusActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                startLocationTracking();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                Snackbar.make(
                        findViewById(R.id.activity_walk_status),
                        R.string.location_denied,
                        Snackbar.LENGTH_INDEFINITE)
                        .show();
            }
        }
    }

    private void walkLocationUpdated(List<WalkLocation> locations) {
        if(locations.size() == 0) return;
        float walkDistance = LocationUtil.getDistance(locations);
        mWalk.setWalkDistance(walkDistance);
        mWalkDistanceText.setText(String.format("%.1f", mWalk.getWalkDistance()));
    }


    private void endWalk() {
        Intent intent = new Intent(this, WalkSummaryActivity.class);
        intent.putExtra(WalkSummaryActivity.WALK_ID, mWalkId);
        intent.putExtra(WalkSummaryActivity.WALK_JUST_FINISHED, true);
        PhotoReminderAlarm.cancelAlarm(this, mWalkId);
        mFusedLocationClient.removeLocationUpdates(locationUtil);
        mWalk.setWalkEndTime(getTime());
        mWalk.setWalkDuration(
                getDurationString(this, mWalk.getWalkEndTime() - mWalk.getWalkStartTime()));
        mWalkieViewModel.updateWalk(mWalk);
        startActivity(intent);
    }
}
