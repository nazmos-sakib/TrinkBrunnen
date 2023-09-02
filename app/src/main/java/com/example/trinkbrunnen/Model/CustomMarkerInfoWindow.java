package com.example.trinkbrunnen.Model;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.trinkbrunnen.Callback.MarkerInfoClickCallback;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

public class CustomMarkerInfoWindow extends MarkerInfoWindow {
    MarkerInfoClickCallback callback;
    public CustomMarkerInfoWindow(MapView mapView, MarkerInfoClickCallback callback) {
        super(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, mapView);

        this.callback = callback;

        mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_image).setOnClickListener(View->{
            callACallback();
        });

        mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_title).setOnClickListener(View->{
            callACallback();
        });

        mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_moreinfo).setOnClickListener(View->{
            callACallback();
        });

        mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_description).setOnClickListener(View->{
            callACallback();
        });

        mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_subdescription).setOnClickListener(View->{
            callACallback();
        });

        mView.setOnClickListener(View->{
            callACallback();
        });
    }


    void callACallback(){
        Log.d("CustomInfoWindow->", "onClick: ");
        callback.onMarkerInfoWindowClick();
    }
}