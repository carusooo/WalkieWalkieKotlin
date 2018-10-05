package com.example.macarus0.walkiewalkie.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
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

import static com.example.macarus0.walkiewalkie.util.TimeStampUtil.calculateDuration;

public class WalkStatusActivity extends AppCompatActivity {

    public static final String WALK_ID = "walk_id";
    public static final String WALK_TRACK_DISTANCE = "walk_track_distance";

    private static final String TAG = "WalkStatusActivity";

    @BindView(R.id.walk_distance_label)
    TextView mWalkDistanceLabel;
    @BindView(R.id.walk_distance_text)
    TextView mWalkDistanceText;


    @BindView(R.id.walk_end_walk_button)
    Button mEndWalkButton;

    private long mWalkId;
    private Walk mWalk;
    private WalkieViewModel mWalkieViewModel;
    private WalkPhotosFragment mWalkPhotosFragment;
    private WalkDogsFragment mWalkDogsFragment;

    private RecyclerView mPhotoReyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_status);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mWalkId = intent.getLongExtra(WALK_ID, -1);

        mWalkieViewModel = ViewModelProviders.of(this).get(WalkieViewModel.class);

        mEndWalkButton.setText(getString(R.string.end_walk));
        mEndWalkButton.setOnClickListener(view -> endWalk());
        mWalkDistanceLabel.setText(getString(R.string.walk_distance_label));


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
        Log.i(TAG, "showWalkUI: " + walk.getDogs());
    }


    private void endWalk() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PhotoReminderAlarm.cancelAlarm(this, mWalkId);
        mWalk.setWalkDuration(calculateDuration(mWalk.getWalkDate()));
        mWalkieViewModel.updateWalk(mWalk);
        startActivity(intent);
    }
}
