package com.example.macarus0.walkiewalkie.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.macarus0.walkiewalkie.R;
import com.example.macarus0.walkiewalkie.data.Walk;
import com.example.macarus0.walkiewalkie.util.PhotoReminderAlarm;
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalkStatusActivity extends AppCompatActivity {

    public static final String WALK_ID = "walk_id";
    public static final String WALK_TRACK_DISTANCE = "walk_track_distance";

    private static final String TAG = "WalkStatusActivity";

    @BindView(R.id.walk_distance_label)
    TextView mWalkDistanceLabel;
    @BindView(R.id.walk_distance_text)
    TextView mWalkDistanceText;

    @BindView(R.id.walk_photos)
    CardView mWalkPhotos;
    @BindView(R.id.walk_dogs)
    CardView mWalkDogs;
    @BindView(R.id.walk_end_walk_button)
    Button mEndWalkButton;

    private long mWalkId;
    private Walk mWalk;
    private WalkieViewModel mWalkieViewModel;

    private RecyclerView mDogRecyclerView;
    private RecyclerView mPhotoReyclerView;

    private CheckedPhotoListAdapter dogPhotoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_status);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mWalkId = intent.getLongExtra(WALK_ID, -1);

        mWalkieViewModel = ViewModelProviders.of(this).get(WalkieViewModel.class);

        mPhotoReyclerView = mWalkPhotos.findViewById(R.id.walk_photos_rv);
        mDogRecyclerView = mWalkDogs.findViewById(R.id.walk_dogs_rv);
        mEndWalkButton.setText(getString(R.string.end_walk));
        mEndWalkButton.setOnClickListener(view -> endWalk());
        mWalkDistanceLabel.setText(getString(R.string.walk_distance_label));


        TextView walkPhotosTitle = mWalkPhotos.findViewById(R.id.walk_photos_title);
        walkPhotosTitle.setText(getString(R.string.walk_photos_title));
        Button takePhotoButton = mWalkPhotos.findViewById(R.id.walk_take_photo_button);
        takePhotoButton.setText(getString(R.string.walk_take_photo));
        takePhotoButton.setOnClickListener(view -> takePhoto());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
                //(int)getResources().getDimension(R.dimen.walk_status_dog_grid_columns));
        mDogRecyclerView.setLayoutManager(gridLayoutManager);
        dogPhotoListAdapter = new CheckedPhotoListAdapter();
        dogPhotoListAdapter.setSupportChecks(false);
        mDogRecyclerView.setAdapter(dogPhotoListAdapter);

        mWalkieViewModel.getWalkById(mWalkId).observe(this, this::showWalkUI);

    }

    private void showWalkUI(Walk walk) {
        mWalk = walk;
        Log.i(TAG, "showWalkUI: " + walk.getDogs());
        dogPhotoListAdapter.setDogs(walk.getDogs());
    }

    private void takePhoto() {

    }

    private void endWalk() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PhotoReminderAlarm.cancelAlarm(this, mWalkId);
        startActivity(intent);
    }
}
