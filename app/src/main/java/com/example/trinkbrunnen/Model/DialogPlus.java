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
    public DialogPlus(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onRecViewItemClick(BookmarkLocationModel locationModel) {
        Toast.makeText(getContext(), "position-> "+ locationModel.getLocationName(), Toast.LENGTH_SHORT).show();
        Map.getInstance().getMapView().getController().setCenter(locationModel.getGeoPoint());

        Marker startMarker = new Marker(Map.getInstance().getMapView());
        startMarker.setPosition(locationModel.getGeoPoint());
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        startMarker.setIcon(context.getDrawable(R.drawable.ic_baseline_bookmark_24));
        startMarker.setTitle("Start point");
        Map.getInstance().getMapView().getOverlays().add(startMarker);



        this.dismiss();
    }
}
