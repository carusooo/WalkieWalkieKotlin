package com.example.macarus0.walkiewalkie.util

import android.content.Context
import android.graphics.Color
import android.location.Location
import android.util.Log
import com.example.macarus0.walkiewalkie.data.WalkLocation
import com.example.macarus0.walkiewalkie.data.WalkieDatabase
import com.example.macarus0.walkiewalkie.data.WalkieDatabaseProvider
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil
import java.util.*

class LocationUtil(context: Context, private var mWalkId: Long) : LocationCallback() {
    private var mDb: WalkieDatabase

    init {
        mDb = WalkieDatabase.getDb(context)
    }

    override fun onLocationResult(locationResult: LocationResult?) {
        Log.i(TAG, "onLocationResult: Received locationResult")
        if (locationResult != null) {
            val locations = locationResult.locations
            val walkLocations = ArrayList<WalkLocation>()
            for (location in locations) {
                // Ignore inaccurate location values
                if (location.accuracy > ACCURACY) continue
                val walkLocation = WalkLocation()
                walkLocation.walkId = mWalkId
                walkLocation.latitude = location.latitude
                walkLocation.longitude = location.longitude
                walkLocation.timestamp = location.time
                walkLocation.accuracy = location.accuracy.toDouble()
                walkLocations.add(walkLocation)
            }
            Log.i(TAG, String.format("onLocationResult: Adding %d locations", walkLocations.size))
            Thread { mDb.walkLocationDao.addWalkLocations(walkLocations) }.start()
        }
    }

    companion object {
        private const val TAG = "LocationUtil"

        private const val MAP_BORDER = .00002
        private const val ACCURACY = 30.0

        private const val MAP_URL = "https://maps.googleapis.com/maps/api/staticmap?"
        private const val MAP_SIZE = "640x480"
        private const val MAP_KEY = "AIzaSyAalNWRPNCOrokAA48IR-blbRHwRS2txbA"

        fun generateMapsUrl(locations: List<WalkLocation>): String {
            val sb = StringBuilder()
            sb.append(MAP_URL)
            sb.append("path=")
            val result = locations.joinToString(separator = "|")
            sb.append(result)
            sb.append(String.format("&size=%s", MAP_SIZE))
            sb.append(String.format("&key=%s", MAP_KEY))
            Log.i(TAG, "getMapURL: " + sb.toString())
            return sb.toString()
        }

        fun getDistance(locations: List<WalkLocation>): Float {
            var distance = 0f
            var lastLocation = Location("")
            lastLocation.latitude = locations[0].latitude
            lastLocation.longitude = locations[0].longitude
            for (location in locations.subList(1, locations.size)) {
                val currentLocation = Location("")
                currentLocation.latitude = location.latitude
                currentLocation.longitude = location.longitude
                distance += lastLocation.distanceTo(currentLocation)
                lastLocation = currentLocation
            }
            return distance
        }

        fun addPathToMap(googleMap: GoogleMap, locations: List<WalkLocation>) {
            val polylineOptions = PolylineOptions().width(3f).color(Color.RED)
            for (walkLocation in locations) {
                polylineOptions.add(LatLng(walkLocation.latitude,
                        walkLocation.longitude))
            }
            googleMap.addPolyline(polylineOptions)
        }

        fun getBounds(locations: List<WalkLocation>): LatLngBounds {
            var north = -90.0
            var east = -180.0
            var south = 90.0
            var west = 180.0
            for (walkLocation in locations) {
                if (walkLocation.latitude > north) north = walkLocation.latitude
                if (walkLocation.latitude < south) south = walkLocation.latitude
                if (walkLocation.longitude > east) east = walkLocation.longitude
                if (walkLocation.longitude < west) west = walkLocation.longitude
            }

            val northeast = LatLng(north + Math.abs(north * MAP_BORDER), east + Math.abs(east * MAP_BORDER))
            val southwest = LatLng(south - Math.abs(south * MAP_BORDER), west - Math.abs(west * MAP_BORDER))
            Log.i(TAG, "getBounds: " + northeast.toString() + " " + southwest.toString())
            return increaseBy(northeast, southwest, 30)
        }

        private fun increaseBy(northeast: LatLng, southwest: LatLng, meters: Int): LatLngBounds {
            val headingSWNE = SphericalUtil.computeHeading(southwest, northeast)
            val newNE = SphericalUtil.computeOffset(northeast, meters.toDouble(), headingSWNE)

            val headingNESW = SphericalUtil.computeHeading(northeast, southwest)
            val newSW = SphericalUtil.computeOffset(southwest, meters.toDouble(), headingNESW)

            return LatLngBounds.builder().include(newNE).include(newSW).build()
        }
    }
}
