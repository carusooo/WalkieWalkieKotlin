package com.example.macarus0.walkiewalkie.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.macarus0.walkiewalkie.R
import com.example.macarus0.walkiewalkie.data.Owner
import com.example.macarus0.walkiewalkie.data.Walk
import com.example.macarus0.walkiewalkie.data.WalkLocation
import com.example.macarus0.walkiewalkie.util.LocationUtil
import com.example.macarus0.walkiewalkie.util.WalkEmailUtil
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink

class WalkSummaryActivity : AppCompatActivity(), OnMapReadyCallback {

    @BindView(R.id.walk_distance_label)
    @JvmField var mWalkDistanceLabel: TextView? = null
    @BindView(R.id.walk_distance_text)
    @JvmField var mWalkDistanceText: TextView? = null


    @BindView(R.id.walk_end_walk_button)
    @JvmField var mShareWalkButton: Button? = null
    @BindView(R.id.walk_skip_summary_button)
    @JvmField var mSkipSharingButton: Button? = null
    @BindView(R.id.walk_map)
    @JvmField var mWalkMap: MapView? = null
    private lateinit var mGoogleMap: GoogleMap

    private var mWalkId: Long = 0
    private lateinit var mWalk: Walk
    private lateinit var mWalkLocations: List<WalkLocation>
    private lateinit var mWalkieViewModel: WalkieViewModel
    private var mWalkPhotosFragment: WalkPhotosFragment? = null
    private var mWalkDogsFragment: WalkDogsFragment? = null
    private lateinit var mWalkOwners: List<Owner>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walk_status)
        ButterKnife.bind(this)
        val intent = intent
        mWalkId = intent.getLongExtra(WALK_ID, -1)
        val walkJustFinished = intent.getBooleanExtra(WALK_JUST_FINISHED, true)

        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }
        mWalkDistanceLabel!!.text = getString(R.string.walk_distance_label)

        mWalkieViewModel = ViewModelProviders.of(this).get(WalkieViewModel::class.java)

        mShareWalkButton!!.text = getString(R.string.walk_share_summary)
        mShareWalkButton!!.setOnClickListener { view -> shareWalk() }
        mShareWalkButton!!.isEnabled = false
        if (walkJustFinished) {
            mSkipSharingButton!!.text = getString(R.string.walk_skip_summary)
            mSkipSharingButton!!.setOnClickListener { view -> skipSharing() }
        } else {
            mSkipSharingButton!!.visibility = View.GONE
        }
        mWalkieViewModel.getWalkById(mWalkId).observe(this, Observer<Walk> { this.showWalkUI(requireNotNull(it)) })
        mWalkieViewModel.getDogOwnersOnWalk(mWalkId).observe(this, Observer<List<Owner>>{ owners ->
            mWalkOwners = requireNotNull(owners)
            enableSharing()
        })
        mWalkMap!!.onCreate(mapViewBundle)

        val fragmentManager = supportFragmentManager
        if (mWalkPhotosFragment == null) {
            mWalkPhotosFragment = WalkPhotosFragment.newInstance(false)
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
    }

    private fun showWalkUI(walk: Walk) {
        mWalk = walk
        Log.i(TAG, "showWalkUI: " + walk.dogs)
        mWalkDistanceText!!.text = String.format("%.1f", mWalk.walkDistance)
        if (mWalk.isDistanceTracked) {
            mWalkMap!!.getMapAsync { this.onMapReady(it) }
            mWalkieViewModel.getLocations(mWalkId).observe(this, Observer<List<WalkLocation>> { this.showPathUI(requireNotNull(it)) })
        }
    }

    private fun createShortlink() {
        val mapsUrl = LocationUtil.generateMapsUrl(mWalkLocations)
        val shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(mapsUrl))
                .setDomainUriPrefix("https://walkiewalkie.page.link")
                .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Short link created
                        val shortLink = task.result!!.shortLink
                        mWalk.setWalkPathLink(shortLink.toString())
                        enableSharing()
                    } else {
                        Log.e(TAG, "onComplete: Unable to create short Url: " + task.exception!!)
                        mWalk.setWalkPathLink(mapsUrl)
                        enableSharing()
                    }
                }
    }

    override fun onStart() {
        mWalkMap!!.onStart()
        super.onStart()
    }

    override fun onStop() {
        mWalkMap!!.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mWalkMap!!.onDestroy()
        super.onDestroy()
    }

    private fun showPathUI(locations: List<WalkLocation>) {
        Log.i(TAG, "showPathUI: Showing Path")
        mWalkLocations = locations
        if (mWalk.walkPathUrl == null) {
            createShortlink()
        }
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LocationUtil.getBounds(locations), 0))
        LocationUtil.addPathToMap(mGoogleMap, locations)
    }

    private fun enableSharing() {
        if (mWalkOwners != null && mWalk.walkPathUrl != null) {
            mShareWalkButton!!.isEnabled = true
        }
    }

    private fun shareWalk() {
        val emailUtil = WalkEmailUtil(this, mWalk, requireNotNull(mWalkPhotosFragment!!.walkPhotos),
                mWalkOwners)
        val intent = emailUtil.emailIntent
        startActivity(Intent.createChooser(intent, getString(R.string.email_intent_title)))
    }

    private fun skipSharing() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
    }

    companion object {

        const val WALK_ID = "walk_id"
        const val WALK_JUST_FINISHED = "walk_just_finished"

        private const val TAG = "WalkSummaryActivity"
        private const val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
    }

}
