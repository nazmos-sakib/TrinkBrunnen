package com.example.trinkbrunnen.Model;

//import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.trinkbrunnen.Callback.LocationLoadedCallback;
import com.example.trinkbrunnen.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.ByteArrayOutputStream;

//Singleton
public class MapOriginCopy {

    private static final String TAG = "Map Model->";
    private static final int PICK_IMAGE_REQUEST_CODE = 9544;

    private MapView mapView;
    private Context context;
    private Fragment fragment;

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

    private static MapOriginCopy instance;

    //start class methods----------------------------------------------------------
    private MapOriginCopy(){}
    public static MapOriginCopy getInstance(Context ctx){
        if (null==instance){
            instance = new MapOriginCopy();
        }

        instance.context = ctx;
        return instance;
    }

    public static MapOriginCopy getInstance(Context ctx, MapView mapView) {
        if (null==instance){
            instance = new MapOriginCopy();
            instance.context = ctx;
            instance.mapView = mapView;
            instance.enableUserCurrentLocation2(instance.defaultGpsUpdateTime);
            instance.setDefaultConfiguration();
            instance.enableRotationGestureOverlay();
            instance.addCompassOverlay();
            instance.addScaleBarOverlay();
        }
        return instance;
    }

    public static MapOriginCopy getInstance(LocationLoadedCallback callback, MapView mapView) {
        if (null!=instance){
            instance.mapView = mapView;
            instance.enableUserCurrentLocation(callback,instance.defaultGpsUpdateTime);
            instance.setDefaultConfiguration();
            instance.enableRotationGestureOverlay();
            instance.addCompassOverlay();
            instance.addScaleBarOverlay();
        }
        return instance;
    }
    public static MapOriginCopy getInstance(MapView mapView) {
        instance.mapView = mapView;
        return instance;
    }

    public static MapOriginCopy getInstance() {
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
        //gpsMyLocationProvider.setLocationUpdateMinDistance(10000); // [m]  // Set the minimum distance for location updates
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

    public void enableUserCurrentLocation2(int gpsUpdateTime){
        mapController = mapView.getController();

        gpsMyLocationProvider2 = new GpsMyLocationProvider(context);
        gpsMyLocationProvider2.setLocationUpdateMinTime(gpsUpdateTime); // [ms] // Set the minimum time interval for location updates

        myLocationOverlay2 = new MyLocationNewOverlay(gpsMyLocationProvider2, mapView);
        myLocationOverlay2.setDrawAccuracyEnabled(true);
        myLocationOverlay2.enableMyLocation();
        myLocationOverlay2.enableFollowLocation();
        myLocationOverlay2.setDrawAccuracyEnabled(true);
        //myLocationOverlay2.setPersonAnchor(3f,4f);

        //most important.
        myLocationOverlay2.enableFollowLocation();
        mapView.getOverlays().add(myLocationOverlay2);

        mapView.getController().setZoom(15f);
        mapView.getController().setCenter(myLocationOverlay2.getMyLocation());

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
    }  //end of eventListenerOverlay

    public void removeEventListenerOverlay(){
        if (mapView.getOverlays().contains(eventsListenerOverlay)){
            mapView.getOverlays().remove(eventsListenerOverlay);
        }
    }

    public void addZoomScrollListener(){
        instance.mapView.addMapListener(new MapListener() {

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
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.map_fragment_direction_add_clear_dialog, null);

        // Set the view to the AlertDialog
        alertDialog.setView(view);

        Button addBtn = view.findViewById(R.id.btn_addToServer_map_dialog);
        addBtn.setOnClickListener(View->{
            //showAddToServerDialog(m,alertDialog);

            showAddToServerDialog(m,alertDialog);




            //alertDialog.cancel();

        });

        //if click dismiss alert dialog and and the marker overlay
        Button clearBtn = view.findViewById(R.id.btn_clearMarker_map_dialog);
        clearBtn.setOnClickListener(View->{
            alertDialog.dismiss();
            mapView.getOverlays().remove(m);
        });

        alertDialog.show();
    }

    private void showAddToServerDialog(Marker m, AlertDialog  firstDialog) {
        final boolean uploadSuccess = false;
        // Create the second AlertDialog object and set the title
        AlertDialog locationDetailsDialog = new AlertDialog.Builder(context)
                .setTitle("Details")
                .setMessage("you can clear the marker by clicking CLEAR button. " +
                        "If you want to add this location to the map click ADD button")
                .create();

        // Inflate the view containing the EditText and Button
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.details_form_upload_to_server_dialog, null);

        // Set the view to the AlertDialog
        locationDetailsDialog.setView(view);

        //chose image to upload text view.
        TextView uploadImg = view.findViewById(R.id.tv_upload_dialogbox);
        uploadImg.setOnClickListener(View->{
            ChoseLocalImage();
        });

        //show chosen image
        //defined in a global variable so that it can be accessed from onActivityResult function
        chosenImageDialogBOx = view.findViewById(R.id.iv_locationPic_dialogbox);


        //
        Button btnSubmit = view.findViewById(R.id.btn_submit_dialogbox);
        btnSubmit.setOnClickListener(View->{
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
                warningEditView.setVisibility(android.view.View.INVISIBLE);
                Log.d(TAG, "showAddToServerDialog: -> before inserting");
                //ParseObject fountainAddToServer = new ParseObject("testDemo");
                ParseObject fountainAddToServer = new ParseObject("fountainLocation");
                fountainAddToServer.put("title",title); //Column name and value
                Log.d(TAG, "showAddToServerDialog: ->title-> "+title);
                fountainAddToServer.put("description",description);
                Log.d(TAG, "showAddToServerDialog: ->description-> "+description);

                ParseGeoPoint pGeoPoint = new ParseGeoPoint( m.getPosition().getLatitude(), m.getPosition().getLongitude());
                fountainAddToServer.put("location", pGeoPoint);
                Log.d(TAG, "showAddToServerDialog: ->pGeoPoint-> "+ pGeoPoint.toString());

                fountainAddToServer.put("userWhoAddedThis", ParseUser.getCurrentUser().getObjectId());
                Log.d(TAG, "showAddToServerDialog: ->userWhoAddedThis-> "+ParseUser.getCurrentUser().getObjectId().toString());

                fountainAddToServer.put("isCurrentlyActive",isFountainActiveView.isChecked());
                Log.d(TAG, "showAddToServerDialog: ->isCurrentlyActive-> "+isFountainActiveView.isChecked());

                //combining string and timestamp for naming file
                //ParseFile parseFile = new ParseFile("image-"+new Timestamp(new Date().getTime()).toString(), data);
                ParseFile parseFile = new ParseFile("image-", data);
                fountainAddToServer.put("image", parseFile);
                Log.d(TAG, "showAddToServerDialog: ->image-> "+parseFile.toString());


                fountainAddToServer.saveInBackground(new SaveCallback() { //upload file to the parse server
                    //using this call back function will return extra information like if it failed or succeed to upload the file
                    @Override
                    public void done(ParseException e) {
                        if (e==null) { // no error occurred
                            Log.d(TAG, "parse server upload->done: -> Succeed");
                            locationDetailsDialog.cancel();
                            //uploadSuccess = true;
                            firstDialog.cancel();
                            mapView.getOverlays().remove(m);
                        } else {
                            e.getMessage();
                            e.getStackTrace();
                        }
                    }
                });
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

    // Method for starting the activity for selecting image from phone storage
    public void ChoseLocalImage() {
        //verifyStoragePermissions(getActivity());
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        //startActivityForResult(Intent.createChooser(intent, "Open Gallery"), PICK_IMAGE_REQUEST_CODE);
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
