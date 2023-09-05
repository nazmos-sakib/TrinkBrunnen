package com.example.trinkbrunnen.Model;

//import static androidx.core.app.ActivityCompat.startActivityForResult;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.trinkbrunnen.Callback.LocationLoadedCallback;
import com.example.trinkbrunnen.Callback.StartActivityForResultCallback;
import com.example.trinkbrunnen.R;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.ByteArrayOutputStream;

//Singleton
public class Map  {

    private static final String TAG = "Map Class->";
    private static final int PICK_IMAGE_REQUEST_CODE = 9544;


    private int defaultGpsUpdateTime = 100;
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


    //addToServer dialog box image view global variable
    ImageView chosenImageDialogBOx;

    //
    StartActivityForResultCallback fragmentCallbackForLaunch;


    private MapSingleton mapInstance;

    private Map(){}
    public  Map(MapView mapView, StartActivityForResultCallback fragment){
        mapInstance = MapSingleton.getInstance(mapView);
        this.fragmentCallbackForLaunch = fragment;


    }



    public void setDefaultConfiguration(){
        this.setMAPNIKTileSource();
        //this.setDefaultTileSource();

        //Changing the loading tile grid colors
        mapInstance.getMapView().getOverlayManager().getTilesOverlay().setLoadingBackgroundColor(android.R.color.black);
        mapInstance.getMapView().getOverlayManager().getTilesOverlay().setLoadingLineColor(Color.argb(255,0,255,0));
        mapInstance.getMapView().getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);

        //scales tiles to the current screen's DPI, helps with readability of labels
        //mapInstance.getMapView().setTilesScaledToDpi(true);
        mapInstance.getMapView().setBuiltInZoomControls(true);
        mapInstance.getMapView().setMultiTouchControls(true);

        //change PersonIcon
        changePersonIcon(R.drawable.ic_navigation_24);

        //only need this for single tap on map closes all open info window
        mapInstance.getMapView().getOverlays().add(
                new MapEventsOverlay(
                        mapInstance.getContext(),
                        new MapEventsReceiver() {
                            @Override
                            public boolean singleTapConfirmedHelper(GeoPoint p) {
                                InfoWindow.closeAllInfoWindowsOn(mapInstance.getMapView());

                                return false;
                            }

                            @Override
                            public boolean longPressHelper(GeoPoint p) {
                                return false;
                            }
                        }
                )
        );

    }


    public void enableUserCurrentLocation(LocationLoadedCallback callback, int gpsUpdateTime){
        gpsMyLocationProvider = new GpsMyLocationProvider(mapInstance.getContext());
        gpsMyLocationProvider.setLocationUpdateMinTime(gpsUpdateTime); // [ms] // Set the minimum time interval for location updates
        //gpsMyLocationProvider.setLocationUpdateMinDistance(10000); // [m]  // Set the minimum distance for location updates
        myLocationOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, mapInstance.getMapView()){
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
        //myLocationOverlay.setPersonAnchor(.5f,.5f);
        //most important.
        myLocationOverlay.enableFollowLocation();
        mapInstance.getMapView().getOverlays().add(myLocationOverlay);
    }

    public void enableUserCurrentLocation2(int gpsUpdateTime){
        mapController = mapInstance.getMapView().getController();

        gpsMyLocationProvider2 = new GpsMyLocationProvider(mapInstance.getContext());
        gpsMyLocationProvider2.setLocationUpdateMinTime(gpsUpdateTime); // [ms] // Set the minimum time interval for location updates

        myLocationOverlay2 = new MyLocationNewOverlay(gpsMyLocationProvider2, mapInstance.getMapView());
        myLocationOverlay2.setDrawAccuracyEnabled(true);
        myLocationOverlay2.enableMyLocation();
        myLocationOverlay2.enableFollowLocation();
        myLocationOverlay2.setDrawAccuracyEnabled(true);
        //myLocationOverlay2.setPersonAnchor(3f,4f);

        //most important.
        myLocationOverlay2.enableFollowLocation();
        mapInstance.getMapView().getOverlays().add(myLocationOverlay2);

        mapInstance.getMapView().getController().setZoom(15f);
        mapInstance.getMapView().getController().setCenter(myLocationOverlay2.getMyLocation());

    }


    public void initResources(){
        mapController = mapInstance.getMapView().getController();
    }


    public void enableRotationGestureOverlay(){
        //enable rotation gestures
        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(mapInstance.getContext(), mapInstance.getMapView());
        mRotationGestureOverlay.setEnabled(true);
        mapInstance.getMapView().setMultiTouchControls(true);
        mapInstance.getMapView().getOverlays().add(mRotationGestureOverlay);
    }

    public void addCompassOverlay(){
        mCompassOverlay = new CompassOverlay(mapInstance.getContext(), new InternalCompassOrientationProvider(mapInstance.getContext()), mapInstance.getMapView());
        mCompassOverlay.enableCompass();
        DisplayMetrics dm = mapInstance.getContext().getResources().getDisplayMetrics();
        mCompassOverlay.setCompassCenter( (float) dm.widthPixels/3-7f,200f);
        //mCompassOverlay.setCompassCenter( 100f,10f);
        mapInstance.getMapView().getOverlays().add(mCompassOverlay);
    }

    public void addScaleBarOverlay(){

        //Map Scale bar overlay
        // how high above is the camera
        DisplayMetrics dm = mapInstance.getContext().getResources().getDisplayMetrics();
        mScaleBarOverlay = new ScaleBarOverlay(mapInstance.getMapView());
        mScaleBarOverlay.setCentred(true);
        // x,y position for setting the overlay
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 20);
        mScaleBarOverlay.setTextSize(50.0f);
        mScaleBarOverlay.setEnableAdjustLength(true);
        mScaleBarOverlay.enableScaleBar();
        //mScaleBarOverlay.
        mapInstance.getMapView().getOverlays().add(mScaleBarOverlay);
    }

    public void addMiniMapOverlay(){
        //add the built-in Minimap
        mMinimapOverlay = new MinimapOverlay(mapInstance.getContext(), mapInstance.getMapView().getTileRequestCompleteHandler());
        DisplayMetrics dm = mapInstance.getContext().getResources().getDisplayMetrics();
        mMinimapOverlay.setWidth(dm.widthPixels / 5);
        mMinimapOverlay.setHeight(dm.heightPixels / 5);
        mMinimapOverlay.setZoomDifference(3); // mini map 3 unit zoomed out than original view
        //optionally, you can set the minimap to a different tile source
        mMinimapOverlay.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapInstance.getMapView().getOverlays().add(mMinimapOverlay);
    }

    public void setDefaultTileSource(){
        mapInstance.getMapView().setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
    }
    public void setMAPNIKTileSource(){
        mapInstance.getMapView().setTileSource(TileSourceFactory.MAPNIK);
    }
    public void setUSGS_SATTileSource(){
        mapInstance.getMapView().setTileSource(TileSourceFactory.USGS_SAT);
    }
    public void setUSGS_TOPOTileSource(){
        mapInstance.getMapView().setTileSource(TileSourceFactory.USGS_TOPO);
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
                InfoWindow.closeAllInfoWindowsOn(mapInstance.getMapView());
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                /*Toast.makeText(mapInstance.getContext(),
                        p.getLatitude() + ", " + p.getLongitude(),
                        Toast.LENGTH_SHORT).show();*/
                Log.d(TAG, "singleTapConfirmedHelper: geo point-> " + p.getLatitude() + ", " + p.getLongitude());
                //mapController.setCenter(p);
                Marker m = new Marker(mapInstance.getMapView());
                m.setPosition(p);
                m.setTextLabelBackgroundColor(
                        Color.TRANSPARENT
                );
                m.setId("userDefinedMarker");
                m.setTextLabelForegroundColor(
                        Color.RED
                );
                //m.setTitle("this is the best place");
                m.setTextLabelFontSize(40);
                m.setIcon(mapInstance.getContext().getResources().getDrawable(R.drawable.ic_push_pin_red));

                m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {
                        showAddClearDialog(m);

                        return true; // Return true to consume the event
                    }
                });

                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mapInstance.getMapView().getOverlays()
                        .add(m);


                return false;
            }
        };

        eventsListenerOverlay = new MapEventsOverlay(mapInstance.getContext(), mapEventsReceiver);
        mapInstance.getMapView().getOverlays().add(eventsListenerOverlay);
    }  //end of eventListenerOverlay

    public void removeEventListenerOverlay(){
        if (mapInstance.getMapView().getOverlays().contains(eventsListenerOverlay)){
            mapInstance.getMapView().getOverlays().remove(eventsListenerOverlay);
        }
    }

    public void addZoomScrollListener(){
        mapInstance.getMapView().addMapListener(new MapListener() {

            @Override
            public boolean onScroll(ScrollEvent event) {
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                //getNearByFountainLocation();
                //Log.d(TAG, "onZoom: its changing");
                return false;
            }
        });
    }

    private void changePersonIcon(int drawableToSet){

        // Create a new BitmapDrawable with your desired icon image
        Drawable iconDrawable = mapInstance.getContext().getResources().getDrawable(drawableToSet);
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
        myLocationOverlay.setPersonAnchor(.5f,.5f);
    }

    //on marker single click call this function
    private void showAddClearDialog(Marker m) {
        AlertDialog alertDialog  = new AlertDialog.Builder(mapInstance.getContext())
                .setTitle("Add Current marker to server")
                .setMessage("you can clear the marker by clicking CLEAR button. " +
                        "If you want to save this marker to the server, click add button")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Clear any data or input fields
                    }
                })
                .create();


        // Inflate the view containing the EditText and Button
        LayoutInflater inflater = LayoutInflater.from(mapInstance.getContext());
        View view = inflater.inflate(R.layout.map_fragment_direction_add_clear_dialog, null);

        // Set the view to the AlertDialog
        alertDialog.setView(view);

        Button addBtn = view.findViewById(R.id.btn_showDirection_map_dialog);
        addBtn.setOnClickListener(View->{
            //showAddToServerDialog(m,alertDialog);

            showAddToServerDialog(m,alertDialog);




            //alertDialog.cancel();

        });

        //if click dismiss alert dialog and and the marker overlay
        Button clearBtn = view.findViewById(R.id.btn_clearMarker_map_dialog);
        clearBtn.setOnClickListener(View->{
            alertDialog.dismiss();
            mapInstance.getMapView().getOverlays().remove(m);
        });

        alertDialog.show();
    }

    private void showAddToServerDialog(Marker m, AlertDialog  firstDialog) {
        // Create the second AlertDialog object and set the title
        AlertDialog locationDetailsDialog = new AlertDialog.Builder(mapInstance.getContext())
                .setTitle("Details")
                .setMessage("you can clear the marker by clicking CLEAR button. " +
                        "If you want to add this location to the map click ADD button")
                .create();

        // Inflate the view containing the EditText and Button
        LayoutInflater inflater = LayoutInflater.from(mapInstance.getContext());
        View view = inflater.inflate(R.layout.dialog_details_form_upload_to_server, null);

        // Set the view to the AlertDialog
        locationDetailsDialog.setView(view);

        ProgressBar pBar = view.findViewById(R.id.progressBar_dialogBox);


        //chose image to upload text view.
        TextView uploadImg = view.findViewById(R.id.tv_upload_dialogbox);
        uploadImg.setOnClickListener(View->{
            //ChoseLocalImage();
            //mTakePhoto.launch("image/*");
            fragmentCallbackForLaunch.onActivityResultLauncher(view.findViewById(R.id.iv_locationPic_dialogbox));
        });

        //show chosen image
        //defined in a global variable so that it can be accessed from onActivityResult function
        chosenImageDialogBOx = view.findViewById(R.id.iv_locationPic_dialogbox);



        //
        Button btnSubmit = view.findViewById(R.id.btn_submit_dialogbox);
        btnSubmit.setOnClickListener(View->{
            pBar.setVisibility(android.view.View.VISIBLE);
            Log.d(TAG, "showAddToServerDialog: -> submit button pressed");
            // Handle the save button click
            // Get the text from the EditText
            EditText titleEditView = view.findViewById(R.id.ev_title_diologbox);
            EditText descriptionEditView = view.findViewById(R.id.ev_description_dialogbox);
            TextView warningEditView = view.findViewById(R.id.tv_warning_dialogbox);

            String title = titleEditView.getText().toString();
            String description = descriptionEditView.getText().toString();

            //checking radioButton
            RadioButton isFountainActiveView = view.findViewById(R.id.radioButtonYes_dialogbox);
            //Boolean isFountainActive = isFountainActiveView.isChecked()? true


            byte[] data = null;

            if (chosenImageDialogBOx.getDrawable() != null){
                Bitmap bitmap = ((BitmapDrawable) chosenImageDialogBOx.getDrawable()).getBitmap();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                data = baos.toByteArray();
            }

            if (title.isEmpty()){
                warningEditView.setVisibility(android.view.View.VISIBLE);
            } else {
                ParseQuarries.appendNewFountainLocation(
                        title,
                        description,
                        m.getPosition(),
                        isFountainActiveView.isChecked(),
                        data,
                        (param)->{
                            pBar.setVisibility(android.view.View.GONE);
                            Log.d(TAG, "parse server upload->done: -> Succeed");
                            locationDetailsDialog.cancel();
                            //uploadSuccess = true;
                            firstDialog.cancel();
                            mapInstance.getMapView().getOverlays().remove(m);
                        }
                );

                Log.d(TAG, "showAddToServerDialog: -> end of inserting");
            }



            // Do something with the text
            /*Toast.makeText(activityContext,
                    text1 + ", " + text2,
                    Toast.LENGTH_SHORT).show();*/
        });



        // upon clicking cancel button it will close this dialog box
        Button btnCancel = view.findViewById(R.id.btn_cancel_dialogbox);
        btnCancel.setOnClickListener(View->{
            locationDetailsDialog.cancel();
        });

        // Create and show the AlertDialog
        locationDetailsDialog.show();
    }



    public void setMiniMapZoomDifference(int z){
        mMinimapOverlay.setZoomDifference(z); // mini map 3 unit zoomed out than original view
    }

    public void setCenter(GeoPoint point){
        mapInstance.getMapView().getController().setCenter(point);
    }

    public void setZoom(float z){
        mapInstance.getMapView().getController().setZoom(z);
    }

    public void setGpsLocationUpdateDistance(int d){
        gpsMyLocationProvider.setLocationUpdateMinDistance(d); // [m]  // Set the minimum distance for location updates
    }

    public void setGpsLocationUpdateTime(int t){
        gpsMyLocationProvider.setLocationUpdateMinTime(t);   // [ms] // Set the minimum time interval for location updates
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

    public MapView getMapView(){
        return mapInstance.getMapView();
    }

    public Marker addMarker(GeoPoint location, String markerId, String title,String description, boolean isFountainMarker,ParseFile markerImage){
        Marker m = new Marker(mapInstance.getMapView());
        m.setId(markerId);
        m.setPosition(location);

        m.setTextLabelBackgroundColor(
                Color.TRANSPARENT
        );
        m.setTextLabelForegroundColor(
                Color.RED
        );
        m.setTitle(title);
        m.setSubDescription(description);
        m.setTextLabelFontSize(40);
        //m.setTextIcon("text");

        if (isFountainMarker){
            m.setIcon(mapInstance.getContext().getResources().getDrawable(R.drawable.ic_water_drop_24));
        } else {
            m.setIcon(mapInstance.getContext().getResources().getDrawable(R.drawable.ic_push_pin_red));
        }

        if (markerImage!=null){
            markerImage.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        // The image data was successfully retrieved
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        // Use the bitmap as needed
                        Drawable drawable = new BitmapDrawable(mapInstance.getContext().getResources(), bitmap);

                        m.setImage(drawable);

                    } else {
                        // There was an error retrieving the image data
                        Log.e(TAG, "Error loading image data: " + e.getMessage());
                    }
                }
            });
        }

        m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);


        mapInstance.getMapView().getOverlays().add(m);

        return m;
    }





}
