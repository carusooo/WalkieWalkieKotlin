package com.example.macarus0.walkiewalkie.view

import android.Manifest
import android.appwidget.AppWidgetManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.macarus0.walkiewalkie.DogWidgetService
import com.example.macarus0.walkiewalkie.R
import com.example.macarus0.walkiewalkie.data.Walk
import com.example.macarus0.walkiewalkie.data.WalkLocation
import com.example.macarus0.walkiewalkie.util.LocationUtil
import com.example.macarus0.walkiewalkie.util.PhotoReminderAlarm
import com.example.macarus0.walkiewalkie.util.TimeStampUtil
import com.example.macarus0.walkiewalkie.view.WalkieDogsWidget
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

class WalkStatusActivity : AppCompatActivity(), OnMapReadyCallback {


    @BindView(R.id.walk_distance_label)
    @JvmField  var mWalkDistanceLabel: TextView? = null
    @BindView(R.id.walk_distance_text)
    @JvmField  var mWalkDistanceText: TextView? = null
    @BindView(R.id.walk_end_walk_button)
    @JvmField  var mEndWalkButton: Button? = null
    @BindView(R.id.walk_skip_summary_button)
    @JvmField var mSkipSharingButton: Button? = null
    @BindView(R.id.walk_map)
    @JvmField  var mWalkMap: MapView? = null
    private var mWalkId: Long = 0
    private var mTrackDistance: Boolean = false
    private var mLocationRequest: LocationRequest? = null
    private lateinit var mWalk: Walk
    private var mWalkPath: List<WalkLocation>? = null
    private lateinit var mGoogleMap: GoogleMap
    private var mWalkieViewModel: WalkieViewModel? = null
    private var mWalkPhotosFragment: WalkPhotosFragment? = null
    private var mWalkDogsFragment: WalkDogsFragment? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationUtil: LocationUtil? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walk_status)
        ButterKnife.bind(this)

        var mapViewBundle: Bundle? = null
        val intent = intent
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
            mWalkId = savedInstanceState.getLong(WALK_ID)
            mTrackDistance = savedInstanceState.getBoolean(WALK_TRACK_DISTANCE)
        } else if (intent != null) {
            mWalkId = intent.getLongExtra(WALK_ID, -1)
            mTrackDistance = intent.getBooleanExtra(WALK_TRACK_DISTANCE, false)
        }
        mWalkieViewModel = ViewModelProviders.of(this).get(WalkieViewModel::class.java)

        mEndWalkButton!!.text = getString(R.string.end_walk)
        mEndWalkButton!!.setOnClickListener { view -> endWalk() }
        mSkipSharingButton!!.visibility = View.GONE

        if (mTrackDistance) {
            mWalkDistanceLabel!!.text = getString(R.string.walk_distance_label)
            mWalkMap!!.onCreate(mapViewBundle)
            mWalkMap!!.getMapAsync { this.onMapReady(it) }
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            startLocationTracking()
        }
        Log.e(TAG, "onCreate: mWalkId $mWalkId")

        mWalkieViewModel!!.getWalkById(mWalkId).observe(this, Observer<Walk> { this.showWalkUI(requireNotNull(it)) })

        val fragmentManager = supportFragmentManager
        if (mWalkPhotosFragment == null) {
            mWalkPhotosFragment = WalkPhotosFragment.newInstance(true)
        }
        mWalkPhotosFragment!!.setWalkId(mWalkId)
        fragmentManager.beginTransaction()
                .replace(R.id.walk_photos, mWalkPhotosFragment!!)
                .commit()
        if (mWalkDogsFragment == null) {
            mWalkDogsFragment = WalkDogsFragment.newInstance()
        }
        mWalkDogsFragment!!.setWalkId(mWalkId)
        fragmentManager.beginTransaction()
                .replace(R.id.walk_dogs, mWalkDogsFragment!!)
                .commit()

        updateWalkWidget(mWalkId)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.e(TAG, "onSaveInstanceState: mWalkId $mWalkId")
        outState.putLong(WALK_ID, mWalkId)
        outState.putBoolean(WALK_TRACK_DISTANCE, mTrackDistance)
    }

    override fun onStart() {
        mWalkMap!!.onStart()
        super.onStart()
    }

    override fun onPause() {
        mWalkMap!!.onPause()

        super.onPause()
    }

    override fun onResume() {
        mWalkMap!!.onResume()
        super.onResume()
    }

    override fun onStop() {
        mWalkMap!!.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mWalkMap!!.onDestroy()
        super.onDestroy()
    }

    private fun showWalkUI(walk: Walk) {
        mWalk = walk
        mWalkDistanceText!!.text = String.format("%.1f", mWalk.walkDistance)
        Log.i(TAG, "showWalkUI: " + walk.dogs)
    }

    private fun startLocationTracking() {

        if (!checkPermissions()) {
            requestPermissions()
        }
        mLocationRequest = LocationRequest()

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        // Note: apps running on "O" devices (regardless of targetSdkVersion) may receive updates
        // less frequently than this interval when the app is no longer in the foreground.
        mLocationRequest!!.interval = UPDATE_INTERVAL

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        // Sets the maximum time when batched location updates are delivered. Updates may be
        // delivered sooner than this interval.
        mLocationRequest!!.maxWaitTime = MAX_WAIT_TIME

        locationUtil = LocationUtil(this, mWalkId)

        try {
            Log.i(TAG, "Starting location tracking $mWalkId")
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, locationUtil!!, null)
            mFusedLocationClient!!.lastLocation
                    .addOnSuccessListener(this) { location ->
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            setMapLocation(location)
                        }
                    }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

        mWalkieViewModel!!.getLocations(mWalkId).observe(this, Observer<List<WalkLocation>>{ locations -> walkLocationUpdated(requireNotNull(locations)) })
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            Snackbar.make(
                    findViewById(R.id.activity_walk_status),
                    R.string.location_request_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok) { view ->
                        // Request permission
                        ActivityCompat.requestPermissions(this@WalkStatusActivity,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                REQUEST_PERMISSIONS_REQUEST_CODE)
                    }
                    .show()
        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(this@WalkStatusActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isEmpty()) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                startLocationTracking()
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
                        .show()
            }
        }
    }

    private fun walkLocationUpdated(locations: List<WalkLocation>) {
        if (locations.isEmpty()) return
        val walkDistance = LocationUtil.getDistance(locations)
        mWalkPath = locations
        mWalk.walkDistance = walkDistance
        mWalkDistanceText!!.text = String.format("%.1f", mWalk.walkDistance)
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LocationUtil.getBounds(locations), 0))
        LocationUtil.addPathToMap(mGoogleMap, mWalkPath!!)
    }

    private fun setMapLocation(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        val cameraPosition = CameraPosition.Builder()
                .target(latLng)
                .zoom(17f)
                .build()
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun endWalk() {
        val intent = Intent(this, WalkSummaryActivity::class.java)
        intent.putExtra(WalkSummaryActivity.WALK_ID, mWalkId)
        intent.putExtra(WalkSummaryActivity.WALK_JUST_FINISHED, true)
        PhotoReminderAlarm.cancelAlarm(this, mWalkId)
        mFusedLocationClient!!.removeLocationUpdates(locationUtil!!)
        mWalk.walkEndTime = TimeStampUtil.time
        mWalk.walkDuration = TimeStampUtil.getDurationString(this, mWalk.walkEndTime - mWalk.walkStartTime)
        mWalkieViewModel!!.updateWalk(mWalk)
        updateWalkWidget(0)
        startActivity(intent)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        try {
            mGoogleMap.isMyLocationEnabled = true
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

    }

    private fun updateWalkWidget(walkId: Long) {

        val updateWidgetIntent = Intent(this, WalkieDogsWidget::class.java)
        updateWidgetIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        updateWidgetIntent.putExtra(DogWidgetService.WALK_ID, walkId)

        val appWidgetManager = AppWidgetManager.getInstance(this)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(this, WalkieDogsWidget::class.java))
        if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
            Log.i(TAG, "updateWalkWidget: Sending the intent to the widget")
            updateWidgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            this.sendBroadcast(updateWidgetIntent)
        }

    }

    companion object {

        const val WALK_ID = "walk_id"
        const val WALK_TRACK_DISTANCE = "walk_track_distance"

        private const val TAG = "WalkStatusActivity"
        private const val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
        /**
         * The desired interval for location updates. Inexact. Updates may be more or less frequent.
         */
        private const val UPDATE_INTERVAL: Long = 5000 // Every 5 seconds.
        /**
         * The fastest rate for active location updates. Updates will never be more frequent
         * than this value, but they may be less frequent.
         */
        private const val FASTEST_UPDATE_INTERVAL: Long = 5000 // Every 5 seconds
        /**
         * The max time before batched results are delivered by location services. Results may be
         * delivered sooner than this interval.
         */
        private val MAX_WAIT_TIME = UPDATE_INTERVAL * 2 // Every 1 minute.

        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }
}
