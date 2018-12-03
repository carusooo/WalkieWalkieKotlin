package com.example.macarus0.walkiewalkie.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.macarus0.walkiewalkie.R;
import com.example.macarus0.walkiewalkie.data.Owner;
import com.example.macarus0.walkiewalkie.data.Walk;
import com.example.macarus0.walkiewalkie.data.WalkLocation;
import com.example.macarus0.walkiewalkie.util.LocationUtil;
import com.example.macarus0.walkiewalkie.util.WalkEmailUtil;
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.macarus0.walkiewalkie.util.LocationUtil.generateMapsUrl;

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
    private List<WalkLocation> mWalkLocations;
    private WalkieViewModel mWalkieViewModel;
    private WalkPhotosFragment mWalkPhotosFragment;
    private WalkDogsFragment mWalkDogsFragment;
    private List<Owner> mWalkOwners;


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
        mShareWalkButton.setEnabled(false);
        if (walkJustFinished) {
            mSkipSharingButton.setText(getString(R.string.walk_skip_summary));
            mSkipSharingButton.setOnClickListener(view -> skipSharing());
        } else {
            mSkipSharingButton.setVisibility(View.GONE);
        }
        mWalkieViewModel.getDogOwnersOnWalk(mWalkId).observe(this, owners -> {
            mWalkOwners = owners;
            enableSharing();
        });
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
        if (mWalk.isDistanceTracked()) {
            mWalkMap.getMapAsync(this::onMapReady);
            mWalkieViewModel.getLocations(mWalkId).observe(this, this::showPathUI);
        }
    }

    private void createShortlink() {
        String mapsUrl = generateMapsUrl(mWalkLocations);
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(mapsUrl))
                .setDomainUriPrefix("https://walkiewalkie.page.link")
                .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            mWalk.setWalkPathLink(shortLink.toString());
                            enableSharing();
                        } else {
                            Log.e(TAG, "onComplete: Unable to create short Url: " + task.getException());
                            mWalk.setWalkPathLink(mapsUrl);
                        }
                    }
                });
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
        mWalkLocations = locations;
        if (mWalk.getWalkPathUrl() == null) {
            createShortlink();
        }
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LocationUtil.getBounds(locations), 0));
        LocationUtil.addPathToMap(mGoogleMap, locations);
    }

    private void enableSharing() {
        if (mWalkOwners != null && mWalk.getWalkPathUrl() != null) {
            mShareWalkButton.setEnabled(true);
        }
    }

    private void shareWalk() {
        WalkEmailUtil emailUtil = new WalkEmailUtil(this, mWalk, mWalkLocations,
                mWalkPhotosFragment.getWalkPhotos(), mWalkOwners);
        Intent intent = emailUtil.getEmailIntent();
        startActivity(Intent.createChooser(intent, getString(R.string.email_intent_title)));
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
