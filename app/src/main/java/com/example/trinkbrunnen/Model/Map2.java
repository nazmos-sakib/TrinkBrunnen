package com.example.trinkbrunnen.Model;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.trinkbrunnen.Callback.LocationLoadedCallback;
import com.example.trinkbrunnen.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

//Singleton
public class Map2 {

    private static final String TAG = "Map Mpdel->";
    private MapView mapView;
    private Context context;

    private GpsMyLocationProvider gpsMyLocationProvider;
    private GpsMyLocationProvider gpsMyLocationProvider2;
    //overlays
    //important
    private IMapController mapController;
    private MyLocationNewOverlay myLocationOverlay;
    private MyLocationNewOverlay myLocationOverlay2;

    private MinimapOverlay mMinimapOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private CompassOverlay mCompassOverlay;

    private MapEventsOverlay eventsListenerOverlay;

    private static Map2 instance;
    private Map2(){}
    public static Map2 getInstance(Context ctx){
        if (null==instance){
            instance = new Map2();
        }

        instance.context = ctx;
        return instance;
    }

    public static Map2 getInstance(MapView mapView) {
        instance.mapView = mapView;
        return instance;
    }
    public static Map2 getInstance() {
        return instance;
    }

    public void setDefaultConfiguration(){
        this.setMAPNIKTileSource();

        //Changing the loading tile grid colors
        mapView.getOverlayManager().getTilesOverlay().setLoadingBackgroundColor(android.R.color.black);
        mapView.getOverlayManager().getTilesOverlay().setLoadingLineColor(Color.argb(255,0,255,0));
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);

        //scales tiles to the current screen's DPI, helps with readability of labels
        mapView.setTilesScaledToDpi(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        //change PersonIcon
        changePersonIcon(R.drawable.ic_navigation_24);

    }


    public void enableUserCurrentLocation(LocationLoadedCallback callback, int gpsUpdateTime){
        gpsMyLocationProvider = new GpsMyLocationProvider(context);
        gpsMyLocationProvider.setLocationUpdateMinTime(gpsUpdateTime); // [ms] // Set the minimum time interval for location updates
        gpsMyLocationProvider.setLocationUpdateMinDistance(10000); // [m]  // Set the minimum distance for location updates
        myLocationOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, mapView){
            @Override
            public void onLocationChanged(Location location, IMyLocationProvider source) {
                super.onLocationChanged(location, source);

                // This is where you can use updated location
                //mapController.setZoom(13f);
                //mapController.setCenter(new GeoPoint(location));
                Log.d(TAG, "onLocationChanged: "+location.toString());
                callback.onLocationLoaded(new GeoPoint(location));
            }
        };
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.setDrawAccuracyEnabled(true);
        //myLocationOverlay.setPersonAnchor(-0.1f,0f);
        //most important.
        myLocationOverlay.enableFollowLocation();
        mapView.getOverlays().add(myLocationOverlay);
    }

    public void enableUserCurrentLocation2(LocationLoadedCallback callback, int gpsUpdateTime){
        gpsMyLocationProvider2 = new GpsMyLocationProvider(context);
        gpsMyLocationProvider2.setLocationUpdateMinTime(gpsUpdateTime); // [ms] // Set the minimum time interval for location updates
        gpsMyLocationProvider2.setLocationUpdateMinDistance(10000); // [m]  // Set the minimum distance for location updates
        myLocationOverlay2 = new MyLocationNewOverlay(gpsMyLocationProvider2, mapView){
            @Override
            public void onLocationChanged(Location location, IMyLocationProvider source) {
                super.onLocationChanged(location, source);

                // This is where you can use updated location
                //mapController.setZoom(13f);
                //mapController.setCenter(new GeoPoint(location));
                Log.d(TAG, "onLocationChanged: "+location.toString());
                callback.onLocationLoaded(new GeoPoint(location));
            }
        };
        myLocationOverlay2.enableMyLocation();
        myLocationOverlay2.setDrawAccuracyEnabled(true);
        //myLocationOverlay.setPersonAnchor(-0.1f,0f);
        //most important.
        myLocationOverlay2.enableFollowLocation();
        mapView.getOverlays().add(myLocationOverlay2);
    }



    public void initResources(){
        mapController = mapView.getController();

    }

    public void enableRotationGestureOverlay(){
        //enable rotation gestures
        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(context, mapView);
        mRotationGestureOverlay.setEnabled(true);
        mapView.setMultiTouchControls(true);
        mapView.getOverlays().add(mRotationGestureOverlay);
    }

    public void addCompassOverlay(){
        mCompassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context), mapView);
        mCompassOverlay.enableCompass();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mCompassOverlay.setCompassCenter( (float) dm.widthPixels/3-7f,200f);
        //mCompassOverlay.setCompassCenter( 100f,10f);
        mapView.getOverlays().add(mCompassOverlay);
    }

    public void addScaleBarOverlay(){

        //Map Scale bar overlay
        // how high above is the camera
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mScaleBarOverlay = new ScaleBarOverlay(mapView);
        mScaleBarOverlay.setCentred(true);
        // x,y position for setting the overlay
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 20);
        mScaleBarOverlay.setTextSize(50.0f);
        mScaleBarOverlay.setEnableAdjustLength(true);
        mScaleBarOverlay.enableScaleBar();
        //mScaleBarOverlay.
        mapView.getOverlays().add(mScaleBarOverlay);
    }

    public void addMiniMapOverlay(){
        //add the built-in Minimap
        mMinimapOverlay = new MinimapOverlay(context, mapView.getTileRequestCompleteHandler());
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mMinimapOverlay.setWidth(dm.widthPixels / 5);
        mMinimapOverlay.setHeight(dm.heightPixels / 5);
        mMinimapOverlay.setZoomDifference(3); // mini map 3 unit zoomed out than original view
        //optionally, you can set the minimap to a different tile source
        mMinimapOverlay.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapView.getOverlays().add(mMinimapOverlay);
    }

    public void setDefaultTileSource(){
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
    }
    public void setMAPNIKTileSource(){
        mapView.setTileSource(TileSourceFactory.MAPNIK);
    }
    public void setUSGS_SATTileSource(){
        mapView.setTileSource(TileSourceFactory.USGS_SAT);
    }
    public void setUSGS_TOPOTileSource(){
        mapView.setTileSource(TileSourceFactory.USGS_TOPO);
    }
    public void setCycleMapTileSource(){
        //mapView.setTileSource(TileSourceFactory.CYCLEMAP);
    }
    public void addEventListenerOverlay(){
        //----------------------------------------------------------
        //on click get the clicked position geo
        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                Toast.makeText(context,
                        p.getLatitude() + ", " + p.getLongitude(),
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "singleTapConfirmedHelper: geo point-> " + p.getLatitude() + ", " + p.getLongitude());
                //mapController.setCenter(p);
                Marker m = new Marker(mapView);
                m.setPosition(p);
                m.setTextLabelBackgroundColor(
                        Color.TRANSPARENT
                );
                m.setTextLabelForegroundColor(
                        Color.RED
                );
                //m.setTitle("this is the best place");
                m.setTextLabelFontSize(40);
                m.setIcon(context.getResources().getDrawable(R.drawable.ic_push_pin_red));

                m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {
                        showAddClearDialog(m);

                        return true; // Return true to consume the event
                    }
                });

                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mapView.getOverlays()
                        .add(m);


                return false;
            }
        };

        eventsListenerOverlay = new MapEventsOverlay(context, mapEventsReceiver);
        mapView.getOverlays().add(eventsListenerOverlay);
    }

    public void removeEventListenerOverlay(){
        mapView.getOverlays().remove(eventsListenerOverlay);
    }

    private void changePersonIcon(int drawableToSet){

        // Create a new BitmapDrawable with your desired icon image
        Drawable iconDrawable = context.getResources().getDrawable(drawableToSet);
        // Convert the VectorDrawable to a Bitmap
        Bitmap iconBitmap = Bitmap.createBitmap(iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(iconBitmap);
        iconDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        iconDrawable.draw(canvas);
        Paint paint = new Paint();
        ColorFilter filter = new LightingColorFilter(Color.BLACK, Color.BLACK);
        paint.setColorFilter(filter);
        canvas.drawBitmap(iconBitmap, 0, 0, paint);
        myLocationOverlay.setPersonIcon( iconBitmap);
    }

    //on marker single click call this function
    private void showAddClearDialog(Marker m) {
        AlertDialog alertDialog  = new AlertDialog.Builder(context)
                .setTitle("Find Direction")
                .setMessage("you can clear the marker by clicking CLEAR button. " +
                        "If you want to go to this location, click Show Direction")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Clear any data or input fields
                    }
                })
                .create();


        // Inflate the view containing the EditText and Button
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.map_fragment_direction_clear_dialog, null);

        // Set the view to the AlertDialog
        alertDialog.setView(view);

        Button directionBtn = view.findViewById(R.id.btn_showDirection_map_dialog);
        directionBtn.setOnClickListener(View->{
            //showAddToServerDialog(m,alertDialog);


            GeoPoint userPosition = new GeoPoint(mapView.getMapCenter().getLatitude(),mapView.getMapCenter().getLongitude());
            GeoPoint markerPosition = m.getPosition();
            Polyline line = new Polyline(mapView);
            line .addPoint(userPosition);
            line .addPoint(markerPosition);
            mapView.getOverlays().add(line);

/*

            RoadManager roadManager = new OSRMRoadManager(ctx);
            ArrayList<GeoPoint> waypoints = new ArrayList<>();
            waypoints.add(startPoint);
            waypoints.add(endPoint);
            Road road = roadManager.getRoad(waypoints);

*/


            alertDialog.cancel();

        });

        //if click dismiss alert dialog and and the marker overlay
        Button clearBtn = view.findViewById(R.id.btn_clear_map_dialog);
        clearBtn.setOnClickListener(View->{
            alertDialog.dismiss();
            mapView.getOverlays().remove(m);
        });

        alertDialog.show();
    }


    public void setMiniMapZoomDifference(int z){
        mMinimapOverlay.setZoomDifference(z); // mini map 3 unit zoomed out than original view
    }

    public void setZoom(float z){
        mapController.setZoom(z);
    }

    public void setGpsLocationUpdateDistance(int d){
        gpsMyLocationProvider.setLocationUpdateMinDistance(d); // [m]  // Set the minimum distance for location updates
    }

    public void setGpsLocationUpdateTime(int t){
        gpsMyLocationProvider.setLocationUpdateMinTime(t);   // [ms] // Set the minimum time interval for location updates
    }




    public Context getContext() {
        return context;
    }

    public GpsMyLocationProvider getGpsMyLocationProvider() {
        return gpsMyLocationProvider;
    }

    public IMapController getMapController() {
        return mapController;
    }

    public MyLocationNewOverlay getMyLocationOverlay() {
        return myLocationOverlay;
    }

    public MinimapOverlay getmMinimapOverlay() {
        return mMinimapOverlay;
    }

    public ScaleBarOverlay getmScaleBarOverlay() {
        return mScaleBarOverlay;
    }

    public CompassOverlay getCompassOverlay() {
        return mCompassOverlay;
    }


    public MapEventsOverlay getEventsListenerOverlay() {
        return eventsListenerOverlay;
    }

    public MapView getMapView() {
        return mapView;
    }
}
