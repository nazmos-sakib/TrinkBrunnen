package com.example.trinkbrunnen.Callback;

import org.osmdroid.util.GeoPoint;

public interface LocationLoadedCallback {
    void onLocationLoaded(GeoPoint location);
}
