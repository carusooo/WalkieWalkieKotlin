package com.example.macarus0.walkiewalkie.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.macarus0.walkiewalkie.R;
import com.example.macarus0.walkiewalkie.data.Dog;
import com.example.macarus0.walkiewalkie.data.Walk;
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartWalkActivity extends AppCompatActivity implements CheckedPhotoListAdapter.DogCheckHandler {

    private final String TAG = "StartWalkActivity";

    private SimpleDateFormat sdf;


    @BindView(R.id.walk_photo_timer_checkbox)
    AppCompatCheckBox mPhotoTimerCheckBox;
    @BindView(R.id.walk_photo_timer_label)
    TextView mPhotoTimerLabel;
    @BindView(R.id.walk_photo_timer_picker)
    AppCompatSpinner mWalkPhotoTimerSpinner;

    @BindView(R.id.walk_track_distance_checkbox)
    AppCompatCheckBox mWalkTrackDistanceCheckbox;
    @BindView(R.id.walk_track_distance_label)
    TextView mWalkTrackDistanceLabel;

    @BindView(R.id.walk_start_dog_card_recycler_view)
    RecyclerView mDogCardRecyclerView;

    @BindView(R.id.start_walk_start_button)
    Button mStartWalkButton;
    private Walk mWalk;
    private WalkieViewModel mWalkieViewModel;
    private HashSet<Dog> mCheckedDogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_walk);
        ButterKnife.bind(this);

        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ",
                getResources().getConfiguration().locale);

        mWalkieViewModel = ViewModelProviders.of(this).get(WalkieViewModel.class);
        mWalk = new Walk();
        mCheckedDogs = new HashSet<>();

        mStartWalkButton.setText(R.string.start_walk);
        mStartWalkButton.setOnClickListener(view -> startWalkPressed());

        mPhotoTimerLabel.setText(R.string.photo_reminder_label);
        mWalkTrackDistanceLabel.setText(R.string.walk_track_distance_label);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mDogCardRecyclerView.setLayoutManager(gridLayoutManager);
        CheckedPhotoListAdapter checkedPhotoListAdapter = new CheckedPhotoListAdapter();
        checkedPhotoListAdapter.setDogCheckHandler(this::onDogCheckChanged);
        mDogCardRecyclerView.setAdapter(checkedPhotoListAdapter);
        mWalkieViewModel.getAllDogs().observe(this, dogs -> checkedPhotoListAdapter.setDogs(dogs));
    }

    private void startWalkPressed() {
        Log.i(TAG, "startWalkPressed: "+ mCheckedDogs);
        mWalk.setWalkDate(sdf.format(new Date()));
        mWalkieViewModel.insertWalkAndDogs(mWalk, new ArrayList<>(mCheckedDogs))
                .observe(this, id -> startWalk(id));
    }

    private void startWalk(long walkId) {
        // TODO: Set up walk reminder
        // TODO: Send track distance preference to the next activity
        Intent intent = new Intent(this, WalkStatusActivity.class);
        intent.putExtra(WalkStatusActivity.WALK_ID, walkId);
        startActivity(intent);
    }

    @Override
    public void onDogCheckChanged(Dog dog, boolean isChecked) {
        if(isChecked) {
            mCheckedDogs.add(dog);
        } else {
            mCheckedDogs.remove(dog);
        }

    }
}
