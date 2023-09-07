package com.example.trinkbrunnen.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.example.trinkbrunnen.R;

import org.osmdroid.util.GeoPoint;

public  class LocalStorageData {
    private static SharedPreferences pref;

    private LocalStorageData() {
    }

    public static SharedPreferences getInstance(Context context){
        if (null==pref){
            pref = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        }
        return pref;
    }

    public static void setLocalLocationData(GeoPoint location){
        SharedPreferences.Editor editor = pref.edit();

        editor.putLong( "KEY_GEOFENCE_LAT", Double.doubleToRawLongBits( location.getLatitude() ));
        editor.putLong( "KEY_GEOFENCE_LON", Double.doubleToRawLongBits( location.getLongitude() ));
        editor.apply();
    }

    public static GeoPoint getLocalLocationData(){
        if ( pref.contains( "KEY_GEOFENCE_LAT" ) && pref.contains( "KEY_GEOFENCE_LON" )) {
            double lat = Double.longBitsToDouble( pref.getLong( "KEY_GEOFENCE_LAT", -1 ));
            double lon = Double.longBitsToDouble( pref.getLong( "KEY_GEOFENCE_LON", -1 ));
            return new GeoPoint(lat,lon);
        }
        return null;
    }
}
