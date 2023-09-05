package com.example.trinkbrunnen.Model;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.trinkbrunnen.Callback.MarkerInfoClickCallback;
import com.example.trinkbrunnen.R;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

public class CustomMarkerInfoWindow extends MarkerInfoWindow {

    public CustomMarkerInfoWindow(MapView mapView, MarkerInfoClickCallback callback) {
        //super(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, mapView);
        super(R.layout.marker_custom_info_window, mapView);

        mView.findViewById(R.id.parentLayout_marker_info).setOnClickListener(View->{
            callback.onMarkerInfoWindowClick();
        });
    }

}