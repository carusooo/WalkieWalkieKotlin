package com.example.macarus0.walkiewalkie.util;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.macarus0.walkiewalkie.data.WalkLocation;
import com.example.macarus0.walkiewalkie.data.WalkieDatabase;
import com.example.macarus0.walkiewalkie.data.WalkieDatabaseProvider;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.util.ArrayList;
import java.util.List;

public class LocationUtil extends LocationCallback {
    WalkieDatabase mDb;
    long mWalkId;
    private static final String TAG = "LocationUtil";

    public LocationUtil(Context context, long walkId) {
        mDb = WalkieDatabaseProvider.getDatabase(context);
        mWalkId = walkId;
    }
    @Override
    public void onLocationResult(LocationResult locationResult) {
        Log.i(TAG, "onLocationResult: Received locationResult");
        if (locationResult != null) {
            List<Location> locations = locationResult.getLocations();
            ArrayList<WalkLocation> walkLocations = new ArrayList<>();
            for(Location location : locations) {
                WalkLocation walkLocation = new WalkLocation();
                walkLocation.setWalkId(mWalkId);
                walkLocation.setLatitude(location.getLatitude());
                walkLocation.setLongitude(location.getLongitude());
                walkLocation.setTimestamp(location.getTime());
                walkLocations.add(walkLocation);
            }
            Log.i(TAG, String.format("onLocationResult: Adding %d locations", walkLocations.size()));
            new Thread(() -> mDb.getWalkLocationDao().addWalkLocations(walkLocations)).start();
        }
    }

    public static float getDistance(List<WalkLocation> locations) {
        float distance = 0;
        Location lastLocation = new Location("");
        lastLocation.setLatitude(locations.get(0).getLatitude());
        lastLocation.setLongitude(locations.get(0).getLongitude());
        for(WalkLocation location : locations.subList(1, locations.size())) {
            Location currentLocation = new Location("");
            currentLocation.setLatitude(location.getLatitude());
            currentLocation.setLongitude(location.getLongitude());
            distance += lastLocation.distanceTo(currentLocation);
            lastLocation = currentLocation;
        }
        return distance;
    }
}
