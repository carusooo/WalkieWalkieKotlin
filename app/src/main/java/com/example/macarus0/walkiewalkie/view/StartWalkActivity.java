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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.macarus0.walkiewalkie.R;
import com.example.macarus0.walkiewalkie.data.Dog;
import com.example.macarus0.walkiewalkie.data.Walk;
import com.example.macarus0.walkiewalkie.util.PhotoReminderAlarm;
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.macarus0.walkiewalkie.util.TimeStampUtil.getStringTimestamp;
import static com.example.macarus0.walkiewalkie.util.TimeStampUtil.getTime;

public class StartWalkActivity extends AppCompatActivity implements CheckedPhotoListAdapter.DogCheckHandler {

    private final String TAG = "StartWalkActivity";
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
    private SimpleDateFormat sdf;
    private Walk mWalk;
    private boolean mTrackDistance;
    private boolean mPhotoReminder;
    private WalkieViewModel mWalkieViewModel;
    private HashSet<Dog> mCheckedDogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_walk);
        ButterKnife.bind(this);

        mWalkieViewModel = ViewModelProviders.of(this).get(WalkieViewModel.class);
        mWalk = new Walk();
        mCheckedDogs = new HashSet<>();

        mStartWalkButton.setText(R.string.start_walk);
        mStartWalkButton.setOnClickListener(view -> startWalkPressed());

        mPhotoTimerLabel.setText(R.string.photo_reminder_label);
        mPhotoReminder = true;
        mPhotoTimerCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> mPhotoReminder = isChecked);
        mWalkTrackDistanceLabel.setText(R.string.walk_track_distance_label);
        mTrackDistance = true;
        mWalkTrackDistanceCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                mTrackDistance = isChecked);

        ArrayList<String> timerValueArray = new ArrayList<String>();
        for (int minutes : getResources().getIntArray(R.array.reminder_times_minutes)) {
            timerValueArray.add(String.format("%d %s", minutes, getString(R.string.photo_reminder_minutes)));
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, timerValueArray);
        mWalkPhotoTimerSpinner.setAdapter(spinnerArrayAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mDogCardRecyclerView.setLayoutManager(gridLayoutManager);
        CheckedPhotoListAdapter checkedPhotoListAdapter = new CheckedPhotoListAdapter();
        checkedPhotoListAdapter.setDogCheckHandler(this::onDogCheckChanged);
        mDogCardRecyclerView.setAdapter(checkedPhotoListAdapter);
        mWalkieViewModel.getAllDogs().observe(this, checkedPhotoListAdapter::setDogs);
    }

    private void startWalkPressed() {
        Log.i(TAG, "startWalkPressed: " + mCheckedDogs);
        mWalk.setWalkDate(getStringTimestamp());
        mWalk.setWalkStartTime(getTime());
        mWalk.setWalkDogsCount(mCheckedDogs.size());
        mWalkieViewModel.insertWalkAndDogs(mWalk, new ArrayList<>(mCheckedDogs))
                .observe(this, this::startWalk);
    }

    private void startWalk(long walkId) {
        int pickerPosition = mWalkPhotoTimerSpinner.getSelectedItemPosition();
        int reminderMinutes = getResources().getIntArray(R.array.reminder_times_minutes)[pickerPosition];
        if(mPhotoReminder) {
            PhotoReminderAlarm.setAlarm(this, walkId, reminderMinutes * 60);
        }

        Intent intent = new Intent(this, WalkStatusActivity.class);
        intent.putExtra(WalkStatusActivity.WALK_ID, walkId);
        intent.putExtra(WalkStatusActivity.WALK_TRACK_DISTANCE, mTrackDistance);
        startActivity(intent);
    }

    @Override
    public void onDogCheckChanged(Dog dog, boolean isChecked) {
        if (isChecked) {
            mCheckedDogs.add(dog);
        } else {
            mCheckedDogs.remove(dog);
        }

    }
}
