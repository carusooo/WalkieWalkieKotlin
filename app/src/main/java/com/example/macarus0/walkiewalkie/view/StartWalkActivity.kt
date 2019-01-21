package com.example.macarus0.walkiewalkie.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatSpinner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SwitchCompat
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.example.macarus0.walkiewalkie.R
import com.example.macarus0.walkiewalkie.data.Dog
import com.example.macarus0.walkiewalkie.data.Walk
import com.example.macarus0.walkiewalkie.util.PhotoReminderAlarm
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel

import java.util.ArrayList
import java.util.HashSet

import butterknife.BindView
import butterknife.ButterKnife
import com.example.macarus0.walkiewalkie.util.TimeStampUtil

class StartWalkActivity : AppCompatActivity() {

    private val TAG = "StartWalkActivity"
    @BindView(R.id.walk_start_options_title)
    @JvmField  var mOptionsTitleTextView: TextView? = null
    @BindView(R.id.walk_photo_timer_switch)
    @JvmField  var mPhotoTimerSwitch: SwitchCompat? = null
    @BindView(R.id.walk_photo_timer_label)
    @JvmField  var mPhotoTimerLabel: TextView? = null
    @BindView(R.id.walk_photo_timer_picker)
    @JvmField  var mWalkPhotoTimerSpinner: AppCompatSpinner? = null
    @BindView(R.id.walk_track_distance_switch)
    @JvmField  var mWalkTrackDistanceSwitch: SwitchCompat? = null
    @BindView(R.id.walk_track_distance_label)
    @JvmField  var mWalkTrackDistanceLabel: TextView? = null
    @BindView(R.id.walk_start_dog_card_recycler_view)
    @JvmField  var mDogCardRecyclerView: RecyclerView? = null
    @BindView(R.id.start_walk_start_button)
    @JvmField  var mStartWalkButton: Button? = null
    private lateinit var mWalk: Walk
    private var mTrackDistance: Boolean = false
    private var mPhotoReminder: Boolean = false
    private lateinit var mWalkieViewModel: WalkieViewModel
    private lateinit var mCheckedDogs: HashSet<Dog>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_walk)
        ButterKnife.bind(this)

        mWalkieViewModel = ViewModelProviders.of(this).get(WalkieViewModel::class.java)
        mWalk = Walk()
        mCheckedDogs = HashSet()

        mStartWalkButton!!.setText(R.string.start_walk)
        mStartWalkButton!!.setOnClickListener { view -> startWalkPressed() }

        mOptionsTitleTextView!!.setText(R.string.walk_options_title)
        mPhotoTimerLabel!!.setText(R.string.photo_reminder_label)
        mPhotoReminder = true
        mPhotoTimerSwitch!!.setOnCheckedChangeListener { buttonView, isChecked -> mPhotoReminder = isChecked }
        mWalkTrackDistanceLabel!!.setText(R.string.walk_track_distance_label)
        mTrackDistance = true
        mWalkTrackDistanceSwitch!!.setOnCheckedChangeListener { buttonView, isChecked -> mTrackDistance = isChecked }

        val timerValueArray = ArrayList<String>()
        for (minutes in resources.getIntArray(R.array.reminder_times_minutes)) {
            timerValueArray.add(String.format("%d %s", minutes, getString(R.string.photo_reminder_minutes)))
        }
        val spinnerArrayAdapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, timerValueArray)
        mWalkPhotoTimerSpinner!!.adapter = spinnerArrayAdapter

        val gridLayoutManager = GridLayoutManager(this, 3)
        mDogCardRecyclerView!!.layoutManager = gridLayoutManager
        val checkedPhotoListAdapter = CheckedPhotoListAdapter()
        checkedPhotoListAdapter.dogCheckHandler  = { dog, isChecked -> this.onDogCheckChanged(dog, isChecked) }
        mDogCardRecyclerView!!.adapter = checkedPhotoListAdapter
        mWalkieViewModel.getAllDogs().observe(this, Observer<List<Dog>> { checkedPhotoListAdapter.setDogs(requireNotNull(it)) })
    }

    private fun startWalkPressed() {
        Log.i(TAG, "startWalkPressed: " + mCheckedDogs)
        if (mCheckedDogs.isEmpty()) {
            Toast.makeText(this, R.string.walk_no_dogs_selected, Toast.LENGTH_SHORT).show()
            return
        }
        mWalk.walkDate = TimeStampUtil.stringTimestamp
        mWalk.walkStartTime = TimeStampUtil.time
        mWalk.walkDogsCount = mCheckedDogs.size
        mWalk.isDistanceTracked = mTrackDistance
        mWalkieViewModel.insertWalkAndDogs(mWalk, ArrayList(mCheckedDogs))
                .observe(this, Observer<Long> { this.startWalk(requireNotNull(it)) })
    }

    private fun startWalk(walkId: Long) {
        val pickerPosition = mWalkPhotoTimerSpinner!!.selectedItemPosition
        val reminderMinutes = resources.getIntArray(R.array.reminder_times_minutes)[pickerPosition]
        if (mPhotoReminder) {
            PhotoReminderAlarm.setAlarm(this, walkId, reminderMinutes * 60)
        }

        val intent = Intent(this, WalkStatusActivity::class.java)
        intent.putExtra(WalkStatusActivity.WALK_ID, walkId)
        intent.putExtra(WalkStatusActivity.WALK_TRACK_DISTANCE, mTrackDistance)
        startActivity(intent)
    }

    private fun onDogCheckChanged(dog: Dog?, isChecked: Boolean) {
        if (isChecked) {
            mCheckedDogs.add(requireNotNull(dog))
        } else {
            mCheckedDogs.remove(dog)
        }

    }
}
