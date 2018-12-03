package com.example.macarus0.walkiewalkie.util;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.util.Pair;

import com.example.macarus0.walkiewalkie.data.Walk;
import com.example.macarus0.walkiewalkie.data.WalkLocation;
import com.example.macarus0.walkiewalkie.data.WalkieDatabase;
import com.example.macarus0.walkiewalkie.data.WalkieDatabaseProvider;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static android.support.constraint.Constraints.TAG;

public class LocationUtil extends LocationCallback {
    WalkieDatabase mDb;
    long mWalkId;
    private static final String TAG = "LocationUtil";

    private static final double MAP_BORDER = .00002d;
    private static final double ACCURACY = 30d;

    private static final String MAP_URL = "https://maps.googleapis.com/maps/api/staticmap?";
    private static final String MAP_SIZE = "640x480";
    private static final String MAP_KEY = "AIzaSyAalNWRPNCOrokAA48IR-blbRHwRS2txbA";

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
                // Ignore inaccurate location values
                if(location.getAccuracy() > ACCURACY) continue;
                WalkLocation walkLocation = new WalkLocation();
                walkLocation.setWalkId(mWalkId);
                walkLocation.setLatitude(location.getLatitude());
                walkLocation.setLongitude(location.getLongitude());
                walkLocation.setTimestamp(location.getTime());
                walkLocation.setAccuracy(location.getAccuracy());
                walkLocations.add(walkLocation);
            }
            Log.i(TAG, String.format("onLocationResult: Adding %d locations", walkLocations.size()));
            new Thread(() -> mDb.getWalkLocationDao().addWalkLocations(walkLocations)).start();
        }
    }

    public static String generateMapsUrl(List<WalkLocation> locations) {
        StringBuilder sb = new StringBuilder();
        sb.append(MAP_URL);
        sb.append("path=");
        String result = locations.stream().map(WalkLocation::toString)
                .collect(Collectors.joining("|"));
        sb.append(result);
        sb.append(String.format("&size=%s", MAP_SIZE));
        sb.append(String.format("&key=%s", MAP_KEY));
        Log.i(TAG, "getMapURL: "+sb.toString());
        return sb.toString();
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

    public static void addPathToMap(GoogleMap googleMap, List<WalkLocation> locations){
        PolylineOptions polylineOptions = new PolylineOptions().width(3).color(Color.RED);
        for(WalkLocation walkLocation : locations){
            polylineOptions.add(new LatLng(walkLocation.getLatitude(),
                    walkLocation.getLongitude()));
        }
        googleMap.addPolyline(polylineOptions);
    }

    public static LatLngBounds getBounds(List<WalkLocation> locations) {
        double north = -90;
        double east = -180;
        double south = 90;
        double west = 180;
        for (WalkLocation walkLocation: locations) {
            if (walkLocation.getLatitude() > north) north = walkLocation.getLatitude();
            if (walkLocation.getLatitude() < south) south = walkLocation.getLatitude();
            if (walkLocation.getLongitude() > east) east = walkLocation.getLongitude();
            if (walkLocation.getLongitude() < west) west = walkLocation.getLongitude();
        }

        LatLng northeast = new LatLng(north+ Math.abs(north*MAP_BORDER), east + Math.abs(east*MAP_BORDER));
        LatLng southwest = new LatLng(south - Math.abs(south*MAP_BORDER), west - Math.abs(west*MAP_BORDER));
        Log.i(TAG, "getBounds: "+northeast.toString() + " " +southwest.toString());
        return increaseBy(northeast, southwest,  30);
    }

    public static LatLngBounds increaseBy(LatLng northeast, LatLng southwest, int meters) {
        double headingSWNE = SphericalUtil.computeHeading(southwest, northeast);
        LatLng newNE = SphericalUtil.computeOffset(northeast, meters, headingSWNE);

        double headingNESW = SphericalUtil.computeHeading(northeast, southwest);
        LatLng newSW = SphericalUtil.computeOffset(southwest, meters, headingNESW);

        return LatLngBounds.builder().include(newNE).include(newSW).build();
    }}
