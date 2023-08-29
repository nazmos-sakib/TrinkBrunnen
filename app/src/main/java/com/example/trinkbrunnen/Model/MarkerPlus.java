package com.example.trinkbrunnen.Model;

import android.content.Context;
import android.view.MotionEvent;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MarkerPlus extends Marker {
    public MarkerPlus(MapView mapView) {
        super(mapView);
    }

    public MarkerPlus(MapView mapView, Context resourceProxy) {
        super(mapView, resourceProxy);
    }


    @Override
    public boolean onLongPress(MotionEvent event, MapView mapView) {
        return super.onLongPress(event, mapView);
    }
}
