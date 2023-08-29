package com.example.trinkbrunnen.Model;

import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.trinkbrunnen.R;
import com.example.trinkbrunnen.Callback.RecyclerViewClickListener;

import org.osmdroid.views.overlay.Marker;



public class DialogPlus extends Dialog implements RecyclerViewClickListener {
    Context context;
    MapSingleton mapSingleton;
    public DialogPlus(@NonNull Context context) {
        super(context);
        this.context = context;
        try {
            mapSingleton = MapSingleton.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRecViewItemClick(BookmarkLocationModel locationModel) {
        Toast.makeText(getContext(), "position-> "+ locationModel.getLocationName(), Toast.LENGTH_SHORT).show();

        mapSingleton.getMapView().getController().setCenter(locationModel.getGeoPoint());


        Marker startMarker = new Marker(mapSingleton.getMapView());
        startMarker.setPosition(locationModel.getGeoPoint());
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        startMarker.setIcon(context.getDrawable(R.drawable.ic_baseline_bookmark_24));
        startMarker.setTitle("Start point");
        mapSingleton.getMapView().getOverlays().add(startMarker);



        this.dismiss();
    }
}
