package com.example.trinkbrunnen.Callback;

import android.location.Location;

import org.osmdroid.util.GeoPoint;

public interface LocationLoadedCallback {
    void onLocationLoaded(Location location);
}
