package com.example.trinkbrunnen.Model;

import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.trinkbrunnen.Callback.Callback;
import com.example.trinkbrunnen.MainActivity;
import com.example.trinkbrunnen.R;
import com.example.trinkbrunnen.Callback.RecyclerViewClickListener;
import com.example.trinkbrunnen.fragments.MapFragment;
import com.parse.ParseObject;

import org.osmdroid.util.GeoPoint;
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
    public void onRecViewItemClick(LocationModel locationModel) {
        Toast.makeText(getContext(), "position-> "+ locationModel.getLocationName(), Toast.LENGTH_SHORT).show();

        mapSingleton.getMapView().getController().setCenter(locationModel.getGeoPoint());


        Marker startMarker = new Marker(mapSingleton.getMapView());
        startMarker.setPosition(locationModel.getGeoPoint());
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        startMarker.setIcon(context.getDrawable(R.drawable.ic_baseline_bookmark_24));
        startMarker.setTitle("Start point");
        startMarker.setId("userSavedMarker");
        mapSingleton.getMapView().getOverlays().add(startMarker);

        //if the distance btn bookmark location and previous location(saved in shared preference) is more than 10kilometer fetch fountain location
        if(distanceBtnTwoGeoPoint(LocalStorageData.getLocalLocationData(),locationModel.getGeoPoint())>10){
            MainActivity.mapFragment.addFountainMarkersToMapView(locationModel.getGeoPoint());
        }



        this.dismiss();
    }


    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distanceBtnTwoGeoPoint(GeoPoint location1 , GeoPoint location2) {

        double lat1 = location1.getLatitude();
        double lat2 = location2.getLatitude();
        double lon1 = location1.getLongitude();
        double lon2 = location2.getLongitude();
        double el1 = 0.0;
        double el2 = 0.0;

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

}
