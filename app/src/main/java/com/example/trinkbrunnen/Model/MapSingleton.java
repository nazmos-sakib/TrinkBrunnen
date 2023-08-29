package com.example.trinkbrunnen.Model;

import android.content.Context;

import org.osmdroid.views.MapView;


public class MapSingleton {
    private static MapSingleton instance;
    private MapView mapView;
    private Context context;

    //start class methods----------------------------------------------------------
    private MapSingleton(){}
    public static MapSingleton getInstance(Context ctx){
        if (null==instance){
            instance = new MapSingleton();
        }

        instance.context = ctx;
        return instance;
    }

    public static MapSingleton getInstance(MapView mapView) {
        instance.mapView = mapView;
        return instance;
    }


    public static MapSingleton getInstance() throws Exception {

        if (null!=instance.context && null!=instance.mapView)
            return instance;
        else throw new Exception("context and mapView are not initiated");
    }


    private void  setMapView(MapView mapView) {
        instance.mapView = mapView;
    }

    public   MapView getMapView(){
        return mapView;
    }

    public Context getContext(){
        return context;
    }
}
