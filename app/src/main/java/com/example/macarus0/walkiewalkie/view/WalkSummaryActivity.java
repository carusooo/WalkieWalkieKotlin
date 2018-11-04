package com.example.macarus0.walkiewalkie.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.location.Location;
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
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalkSummaryActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String WALK_ID = "walk_id";
    public static final String WALK_JUST_FINISHED = "walk_just_finished";

    private static final String TAG = "WalkSummaryActivity";
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @BindView(R.id.walk_distance_label)
    TextView mWalkDistanceLabel;
    @BindView(R.id.walk_distance_text)
    TextView mWalkDistanceText;


    @BindView(R.id.walk_end_walk_button)
    Button mShareWalkButton;
    @BindView(R.id.walk_skip_summary_button)
    Button mSkipSharingButton;
    @BindView(R.id.walk_map)
    MapView mWalkMap;
    private GoogleMap mGoogleMap;

    private long mWalkId;
    private Walk mWalk;
    private WalkieViewModel mWalkieViewModel;
    private WalkPhotosFragment mWalkPhotosFragment;
    private WalkDogsFragment mWalkDogsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_status);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mWalkId = intent.getLongExtra(WALK_ID, -1);
        boolean walkJustFinished = intent.getBooleanExtra(WALK_JUST_FINISHED, true);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mWalkDistanceLabel.setText(getString(R.string.walk_distance_label));

        mWalkieViewModel = ViewModelProviders.of(this).get(WalkieViewModel.class);

        mShareWalkButton.setText(getString(R.string.walk_share_summary));
        mShareWalkButton.setOnClickListener(view -> shareWalk());
        if(walkJustFinished){
            mSkipSharingButton.setText(getString(R.string.walk_skip_summary));
            mSkipSharingButton.setOnClickListener(view -> skipSharing());
        } else {
            mSkipSharingButton.setVisibility(View.GONE);
        }
        mWalkMap.onCreate(mapViewBundle);

        mWalkieViewModel.getWalkById(mWalkId).observe(this, this::showWalkUI);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (mWalkPhotosFragment == null) {
            mWalkPhotosFragment = WalkPhotosFragment.newInstance(false);
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
        Log.i(TAG, "showWalkUI: " + walk.getDogs());
        mWalkDistanceText.setText(String.format("%.1f", mWalk.getWalkDistance()));
        if(mWalk.isDistanceTracked()) {
            mWalkMap.getMapAsync(this::onMapReady);
            mWalkieViewModel.getLocations(mWalkId).observe(this, this::showPathUI);
        }
    }

    @Override
    protected void onStart() {
        mWalkMap.onStart();
        super.onStart();
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

    private void showPathUI(List<WalkLocation> locations) {
        Log.i(TAG, "showPathUI: Showing Path");
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LocationUtil.getBounds(locations), 0));
        LocationUtil.addPathToMap(mGoogleMap, locations);
    }

    private void shareWalk() {
        skipSharing();
    }

    private void skipSharing() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

}
