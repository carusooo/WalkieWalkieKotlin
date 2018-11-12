package com.example.macarus0.walkiewalkie.view;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.macarus0.walkiewalkie.util.TimeStampUtil.getDurationString;
import static com.example.macarus0.walkiewalkie.util.TimeStampUtil.getTime;

public class WalkStatusActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String WALK_ID = "walk_id";
    public static final String WALK_TRACK_DISTANCE = "walk_track_distance";

    private static final String TAG = "WalkStatusActivity";
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL = 5000; // Every 5 seconds.
    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value, but they may be less frequent.
     */
    private static final long FASTEST_UPDATE_INTERVAL = 5000; // Every 5 seconds
    /**
     * The max time before batched results are delivered by location services. Results may be
     * delivered sooner than this interval.
     */
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 2; // Every 1 minute.

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;


    @BindView(R.id.walk_distance_label)
    TextView mWalkDistanceLabel;
    @BindView(R.id.walk_distance_text)
    TextView mWalkDistanceText;
    @BindView(R.id.walk_end_walk_button)
    Button mEndWalkButton;
    @BindView(R.id.walk_skip_summary_button)
    Button mSkipSharingButton;
    @BindView(R.id.walk_map)
    MapView mWalkMap;
    private long mWalkId;
    private boolean mTrackDistance;
    private LocationRequest mLocationRequest;
    private Walk mWalk;
    private List<WalkLocation> mWalkPath;
    private Location lastLocation;
    private GoogleMap mGoogleMap;
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

        Bundle mapViewBundle = null;
        Intent intent = getIntent();
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
            mWalkId = savedInstanceState.getLong(WALK_ID);
            mTrackDistance = savedInstanceState.getBoolean(WALK_TRACK_DISTANCE);
        } else if (intent != null) {
            mWalkId = intent.getLongExtra(WALK_ID, -1);
            mTrackDistance = intent.getBooleanExtra(WALK_TRACK_DISTANCE, false);
        }
        mWalkieViewModel = ViewModelProviders.of(this).get(WalkieViewModel.class);

        mEndWalkButton.setText(getString(R.string.end_walk));
        mEndWalkButton.setOnClickListener(view -> endWalk());
        mSkipSharingButton.setVisibility(View.GONE);

        if (mTrackDistance) {
            mWalkDistanceLabel.setText(getString(R.string.walk_distance_label));
            mWalkMap.onCreate(mapViewBundle);
            mWalkMap.getMapAsync(this::onMapReady);
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            startLocationTracking();
        }
        Log.e(TAG, "onCreate: mWalkId "+mWalkId);

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState: mWalkId "+mWalkId);
        outState.putLong(WALK_ID, mWalkId);
        outState.putBoolean(WALK_TRACK_DISTANCE, mTrackDistance);
    }

    @Override
    protected void onStart() {
        mWalkMap.onStart();
        super.onStart();
    }

    @Override
    protected void onPause() {
        mWalkMap.onPause();

        super.onPause();
    }

    @Override
    protected void onResume() {
        mWalkMap.onResume();
        super.onResume();
    }

    @Override
    protected void onStop() {
        mWalkMap.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mWalkMap.onDestroy();
        super.onDestroy();
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
        mLocationRequest.setInterval(UPDATE_INTERVAL);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Sets the maximum time when batched location updates are delivered. Updates may be
        // delivered sooner than this interval.
        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);

        locationUtil = new LocationUtil(this, mWalkId);

        try {
            Log.i(TAG, "Starting location tracking "+ mWalkId);
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationUtil, null);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                setMapLocation(location);
                            }
                        }
                    });
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
        mWalkPath = locations;
        mWalk.setWalkDistance(walkDistance);
        mWalkDistanceText.setText(String.format("%.1f", mWalk.getWalkDistance()));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LocationUtil.getBounds(locations), 0));
        LocationUtil.addPathToMap(mGoogleMap, mWalkPath);
    }

    private void setMapLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(17)
                .build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        try {
            mGoogleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
