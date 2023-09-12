package com.example.trinkbrunnen.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trinkbrunnen.BuildConfig;
import com.example.trinkbrunnen.Callback.LocationLoadedCallback;
import com.example.trinkbrunnen.Callback.StartActivityForResultCallback;
import com.example.trinkbrunnen.Callback.UploadingBookmarkFinishCallback;
import com.example.trinkbrunnen.MapboxTestNavigation;
import com.example.trinkbrunnen.Model.CustomMarkerInfoWindow;
import com.example.trinkbrunnen.Model.GeoPointExtra;
import com.example.trinkbrunnen.Model.LocalStorageData;
import com.example.trinkbrunnen.Model.Map;
import com.example.trinkbrunnen.Model.MapSingleton;
import com.example.trinkbrunnen.Model.ParseQuarries;
import com.example.trinkbrunnen.R;
import com.example.trinkbrunnen.databinding.FragmentMapBinding;
import com.parse.ParseObject;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment implements LocationLoadedCallback, UploadingBookmarkFinishCallback, StartActivityForResultCallback {
    private static final String TAG = "MapFragment->";

    FragmentMapBinding binding;
    Context ctx;

    public static Map map;
    GeoPoint destination = null;
    public static GeoPoint mapCameraPosition=null;

    AlertDialog alertDialog;

    //addToServer dialog box image view global variable
    ImageView chosenImageDialogBOx;
    //startActivityForResult
    ActivityResultLauncher<String> mTakePhoto ;

    boolean mTrackingMode = false;
    boolean isFountainMarkerLoaded = false;


    public MapFragment(Context ctx) {
        // Required empty public constructor
        this.ctx = ctx;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle permissions first, before map is created. not depicted here

        //Adjust the size of the cache on disk The primary usage is downloaded map tiles
        //this will set the disk cache size in MB to 1GB , 9GB trim size
        //OpenStreetMapTileProviderConstants. (1000L, 900L);
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        //load/initialize the osmdroid configuration, this can be done
        Configuration.getInstance().load(this.ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setOsmdroidBasePath(ctx.getCacheDir());
        Configuration.getInstance().setOsmdroidTileCache(ctx.getCacheDir());

        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        //inflate and create the map

        //opening image gallery to chose image.
        mTakePhoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        chosenImageDialogBOx.setImageURI(result);
                    }
                }
        );

        //
        isFountainMarkerLoaded = false;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_map, container, false);

        binding = FragmentMapBinding.inflate(inflater,container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //map = Map.getInstance();
        map = new  Map(binding.mapViewMapFragment,this);

        map.enableUserCurrentLocation(this,100);
        map.setDefaultConfiguration();
        map.enableRotationGestureOverlay();
        map.addCompassOverlay();
        map.addScaleBarOverlay();
        //map.addMiniMapOverlay();
        //map.addEventListenerOverlay();

        map.initResources();

        map.setZoom(15f);


        //floating action button
        setFABClickListener();
        setSearchViewClickedListener();
        setOnTouchClickListenerToRootView();

        //setting popUp menu for layer

    }




    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        try {
            MapSingleton.getInstance().getMapView().onResume(); //needed for compass, my location overlays, v6.0.0 and up
        } catch (Exception e) {
            e.printStackTrace();
        }

        //enable getting user current location
        map.getMyLocationOverlay().enableMyLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        try {
            MapSingleton.getInstance().getMapView().onPause();  //needed for compass, my location overlays, v6.0.0 and up
        } catch (Exception e) {
            e.printStackTrace();
        }

        //disable consistently getting user current location
        map.getMyLocationOverlay().disableMyLocation();
    }



    private void setFABClickListener(){
        //binding.fabStandPositionMainActivity.setBackgroundDrawable(ContextCompat.getDrawable(ctx, R.drawable.floating_button_bg));

        //current position. on click set map camera to users current location
        binding.fabStandPositionMainActivity.setOnClickListener(View -> {
            map.setZoom(16f);
            map.setCenter(map.getMyLocationOverlay().getMyLocation());
        });

        //mTrackingMode
        binding.fabFollowUser.setOnClickListener(View->{
            mTrackingMode = !mTrackingMode;
            if (mTrackingMode){
                binding.fabFollowUser.setImageResource(R.drawable.ic_foloow_enable_24);
            } else {
                binding.fabFollowUser.setImageResource(R.drawable.ic_follow_disabled_24);
            }
        });

        //layer
        binding.fabLayer.setOnClickListener(View-> {

            PopupMenu popupMenu = new PopupMenu(getContext(), View);
            popupMenu.getMenuInflater().inflate(R.menu.menu_map_layout_option, popupMenu.getMenu());
            popupMenu.setGravity(2);
            popupMenu.setForceShowIcon(true);
            //changing default style
            for (int i = 0; i < popupMenu.getMenu().size(); i++) {
                MenuItem item = popupMenu.getMenu().getItem(i);
                SpannableString spannableString = new SpannableString(item.getTitle().toString());
                spannableString.setSpan(new TextAppearanceSpan(ctx, R.style.PopupMenuTextAppearance), 0, spannableString.length(), 0);
                item.setTitle(spannableString);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_layout_standArd:
                            // Do something when option 1 is clicked
                            map.setDefaultTileSource();
                            map.getMapView().invalidate();
                            return true;
                        case R.id.menu_layout_satellite:
                            // Do something when option 2 is clicked
                            map.setUSGS_SATTileSource();
                            map.getMapView().invalidate();
                            return true;
                        case R.id.menu_layout_cancel:
                            // Do something when option 2 is clicked
                            return false;
                        default:
                            return false;
                    }
                }
            });
            popupMenu.show();
        });

        //clear direction marker
        //set navigation fab to invisible and set itself invisible
        binding.fabClearDirectionMapFragment.setOnClickListener(View->{
            onDirectionClearMarker();
            map.getMapView().invalidate();
            binding.fabClearDirectionMapFragment.setVisibility(android.view.View.INVISIBLE);
            binding.fabNavigate.setVisibility(android.view.View.INVISIBLE);

        });


        //navigation
        binding.fabNavigate.setOnClickListener(View-> {
            if (destination != null){
                /*getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer_mainActivity,new MapBoxNavigationFragment(getContext(),destination))
                        .commit();*/


                //to open MapBox map

                Intent intent = new Intent(getActivity(), MapboxTestNavigation.class);
                GeoPointExtra d = new GeoPointExtra(destination);
                //intent.putExtra("destination",d);
                intent.putExtra("lat",destination.getLatitude());
                intent.putExtra("lon",destination.getLongitude());
                startActivity(intent);
/*

                //to open google map
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q="+destination.getLatitude()+","+destination.getLongitude()+"&mode=b"));
                intent.setPackage("com.google.android.apps.maps");
                if (intent.resolveActivity(ctx.getPackageManager()) != null){ //check if google map exist or not

                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
*/

            }
        });
    }//end of setFABClickListener

    private void setSearchViewClickedListener(){
        //search button
        binding.btnSearchSearchView.setOnClickListener(View->{
            Toast.makeText(getContext(), binding.edtSearchLocationSearchView.getText(), Toast.LENGTH_SHORT).show();

            Geocoder geocoder = new Geocoder(ctx);
            try {
                String searchText = binding.edtSearchLocationSearchView.getText().toString();
                List<Address> addresses = geocoder.getFromLocationName(searchText, 1);
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    GeoPoint geoPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
                    Log.d(TAG, "setClickedListener: "+address.getCountryName());
                    map.setZoom(12f);
                    map.setCenter(geoPoint);
                    //if the distance btn search location and previous location(saved in shared preference) is more than 10kilometer fetch fountain location
                    if(distanceBtnTwoGeoPoint(LocalStorageData.getLocalLocationData(),geoPoint)>10){
                        addFountainMarkersToMapView(geoPoint);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        //edit text
        binding.edtSearchLocationSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    binding.btnClearSearchView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //clear button
        binding.btnClearSearchView.setOnClickListener(View->{
            binding.edtSearchLocationSearchView.setText("");
            binding.btnClearSearchView.setVisibility(android.view.View.INVISIBLE);
        });
    }//end setSearchViewClickedListener

    double mSpeed = 0.0;
    float mAzimuthAngleSpeed;

    public void azimuCalculation(Location location){

        GeoPoint prevLocation = map.getMyLocationOverlay().getMyLocation();
        map.getDirectedLocationOverlay().setLocation (new GeoPoint(location));
        map.getDirectedLocationOverlay().setAccuracy((int) location.getAccuracy());

        if (prevLocation != null && location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            mSpeed = location.getSpeed() * 3.6;
            long speedInt = Math.round(mSpeed);
            TextView speedTxt = binding.tvSpeedMapFragment;
            speedTxt.setText(speedInt + " km/h");

            //TODO: check if speed is not too small
            if (mSpeed >= 0.1) {
                mAzimuthAngleSpeed = location.getBearing();
                map.getDirectedLocationOverlay().setBearing(mAzimuthAngleSpeed);
            }
        }

        if (mTrackingMode) {
            //keep the map view centered on current location:
            map.getMapView().getController().animateTo(new GeoPoint(location));
            map.getMapView().setMapOrientation(-mAzimuthAngleSpeed);
        } else {
            //just redraw the location overlay:
            map.getMapView().invalidate();
        }



    }



    //callback function from class Map.enableUserCurrentLocation() function
    //when a precise location is found fetch available fountain location within range of 30 kilometer
    @Override
    public void onLocationLoaded(Location location) {

        //if there is no previous location search for fountain location for current location
        //if previous location is found check if fountain location have already retrieved or not
        GeoPoint previousLocation = LocalStorageData.getLocalLocationData();

        if (previousLocation!=null){
            if (!isFountainMarkerLoaded){
                //in Shared Preference previous location found
                //but the fragment reloaded of open newly.
                //need to retrieved fountain locations
                addFountainMarkersToMapView(new GeoPoint(location));
                map.setCenter(new GeoPoint(location));
            }
            //if the distance is not 0 then the user moved and we need to change map center
            //change previous location to new location
            if (distanceBtnTwoGeoPoint(new GeoPoint(location),previousLocation)>0 && mTrackingMode){
                map.setCenter(new GeoPoint(location));
                LocalStorageData.setLocalLocationData(new GeoPoint(location));
            }

        } else {
            Log.d(TAG, "onLocationLoaded:-> no previous location found");
            LocalStorageData.setLocalLocationData(new GeoPoint(location));
            map.setCenter(new GeoPoint(location));
            addFountainMarkersToMapView(new GeoPoint(location));
            azimuCalculation(location);
            return;
        }

        //
        azimuCalculation(location);

        // get the bounding box of the visible area
        BoundingBox boundingBox = map.getMapView().getBoundingBox();

        // calculate the visible area in square kilometers
        double area = boundingBox.getLatitudeSpan() * boundingBox.getLongitudeSpan() * Math.cos(Math.toRadians(boundingBox.getCenter().getLatitude())) * 111.319;

    }//end of onLocationLoad()

    public void addFountainMarkersToMapView(GeoPoint location){
        ParseQuarries.fetchFountainLocation(location,
                (objects)->{

                    for (ParseObject obj: objects){

                        Marker m = map.addMarker(
                                new GeoPoint(obj.getParseGeoPoint("location").getLatitude(),obj.getParseGeoPoint("location").getLongitude()),
                                "fountain_marker+"+obj.getObjectId(),
                                obj.getString("title"),
                                obj.getString("description"),
                                true,
                                obj.getParseFile("image")
                        );

                /*    MarkerInfoWindow infoWindow = new MarkerInfoWindow(org.osmdroid.library.R.layout.bonuspack_bubble , map.getMapView());
                    m.setInfoWindow(infoWindow);
                    */
                        //custom info window.
                        //clicking anywhere in info window calls a callback function
                        m.setInfoWindow(new CustomMarkerInfoWindow(map.getMapView(),
                                ()->{
                                    showDialogOnMarkerSingleClick(m);
                                }
                        ));

                    }
                    isFountainMarkerLoaded = true;

                });
    } //end of addFountainMarkersToMapView()


    //on marker infoWindow single click call this function
    private void showDialogOnMarkerSingleClick(Marker m) {
        alertDialog  = new AlertDialog.Builder(ctx)
                .setTitle("Find Direction")
                .setMessage("you can clear the marker by clicking CLEAR button. " +
                        "If you want to go to this location, click Show Direction")
                .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onDirectionClearMarker();
                        dialog.dismiss();
                        map.getMapView().invalidate();
                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Clear any data or input fields
                    }
                })
                .create();


        // Inflate the view containing the EditText and Button
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.dialog_map_fragment_direction_edit_bookmark_clear, null);

        // Set the view to the AlertDialog
        alertDialog.setView(view);

        Button directionBtn = view.findViewById(R.id.btn_showDirection_map_dialog);
        directionBtn.setOnClickListener(View->{
            createDirection(m);

            alertDialog.cancel();

        });

        Button editBtn = view.findViewById(R.id.btn_editMarker_map_dialog);
        editBtn.setOnClickListener(View->{
            onEditMarker(m);

            alertDialog.cancel();

        });

        /*//if click dismiss alert dialog and and the marker overlay
        Button clearBtn = view.findViewById(R.id.btn_clear_map_dialog);
        clearBtn.setOnClickListener(View->{
            onDirectionClearMarker();
            alertDialog.dismiss();
            //binding.map.getOverlays().remove(m);
            map.getMapView().invalidate();
        });
        */

        //if click dismiss alert dialog and and the marker overlay
        Button addBookmark = view.findViewById(R.id.btn_clearMarker_map_dialog);
        addBookmark.setOnClickListener(View->{
            //Toast.makeText(ctx,"added",Toast.LENGTH_SHORT).show();
            binding.progressBarMapFragment.setVisibility(android.view.View.VISIBLE);
            ParseQuarries.uploadNewBookmark(MapFragment.this,m.getTitle(),m.getPosition());
            //alertDialog.dismiss();
        });

        alertDialog.show();
    } //end of showDialogOnMarkerSingleClick()

    //
    private void onEditMarker(Marker m){

        AlertDialog editDialog = new AlertDialog.Builder(ctx)
                .setTitle("Details")
                .setMessage("you can clear the marker by clicking CLEAR button. " +
                        "If you want to add this location to the map click ADD button")
                .create();

        // Inflate the view containing the EditText and Button
        View view = LayoutInflater.from(ctx)
                .inflate(R.layout.dialog_details_form_upload_to_server, null);

        // Set the view to the AlertDialog
        editDialog.setView(view);

        ProgressBar pBar = view.findViewById(R.id.progressBar_dialogBox);

        //set old title
        EditText ev_title = view.findViewById(R.id.ev_title_diologbox);
        ev_title.setText(m.getTitle());

        //set old description
        EditText ev_description = view.findViewById(R.id.ev_description_dialogbox);
        ev_description.setText(m.getSubDescription());

        //set old fountain active

        //show chosen image
        //defined in a global variable so that it can be accessed from onActivityResult function
        chosenImageDialogBOx = view.findViewById(R.id.iv_locationPic_dialogbox);
        chosenImageDialogBOx.setImageDrawable(m.getImage());

        //chose image to upload text view.
        TextView uploadImg = view.findViewById(R.id.tv_upload_dialogbox);
        uploadImg.setOnClickListener(View->{
            //ChoseLocalImage();
            //mTakePhoto.launch("image/*");
            onActivityResultLauncher(view.findViewById(R.id.iv_locationPic_dialogbox));
        });

        //
        Button btnSubmit = view.findViewById(R.id.btn_submit_dialogbox);
        btnSubmit.setOnClickListener(View->{
            // Handle the save button click
            pBar.setVisibility(android.view.View.VISIBLE);
            Log.d(TAG, "showAddToServerDialog: -> submit button pressed");


            String title = ev_title.getText().toString();
            String description = ev_description.getText().toString();

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
                // show warning
                TextView warningEditView = view.findViewById(R.id.tv_warning_dialogbox);
                warningEditView.setVisibility(android.view.View.VISIBLE);
            } else {
                ParseQuarries.alterFountainData(
                        m.getId(),
                        title,
                        description,
                        isFountainActiveView.isChecked(),
                        data,
                        (param)->{
                            pBar.setVisibility(android.view.View.GONE);
                            Log.d(TAG, "parse server upload->done: -> Succeed");
                            //locationDetailsDialog.cancel();
                            //uploadSuccess = true;
                            editDialog.cancel();
                            //mapInstance.getMapView().getOverlays().remove(m);
                        }
                );

                Log.d(TAG, "showAddToServerDialog: -> end of inserting");
            }

        });



        // upon clicking cancel button it will close this dialog box
        Button btnCancel = view.findViewById(R.id.btn_cancel_dialogbox);
        btnCancel.setOnClickListener(View->{
            editDialog.cancel();
        });

        // Create and show the AlertDialog
        editDialog.show();
    } //end onEditMarker()


    @Override
    public void onBookmarkUploadFinish() {
        alertDialog.dismiss();
        binding.progressBarMapFragment.setVisibility(View.INVISIBLE);
        Toast.makeText(getContext(),"added",Toast.LENGTH_SHORT).show();
    }

    public void createDirection(Marker m){
        RoadManager roadManager = new OSRMRoadManager(ctx, Configuration.getInstance().getUserAgentValue());
        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(map.getMyLocationOverlay().getMyLocation());
        GeoPoint endPoint = m.getPosition();
        destination = m.getPosition();
        waypoints.add(endPoint);

        //
        ((OSRMRoadManager)roadManager).setMean(OSRMRoadManager.MEAN_BY_FOOT);


        Road road = roadManager.getRoad(waypoints);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        roadOverlay.setId("direction_overlay");
        map.getMapView().getOverlays().add(roadOverlay);
        map.getMapView().invalidate();

        Drawable nodeIcon = getResources().getDrawable(R.drawable.ic_marker_node_24);
        for (int i=0; i<road.mNodes.size(); i++){
            RoadNode node = road.mNodes.get(i);
            Marker nodeMarker = new Marker(map.getMapView());
            nodeMarker.setId("direction_node");
            nodeMarker.setPosition(node.mLocation);
            nodeMarker.setIcon(nodeIcon);
            nodeMarker.setTitle("Step "+i);
            nodeMarker.setSnippet(node.mInstructions);
            Log.d(TAG, "createDirection: "+node.mInstructions);
            Log.d(TAG, "createDirection: "+node.mManeuverType);
            Log.d(TAG, "createDirection: "+node.mNextRoadLink);
            Log.d(TAG, "createDirection: "+node.describeContents());
            nodeMarker.setSubDescription(Road.getLengthDurationText(ctx, node.mLength, node.mDuration));
            nodeMarker.setImage(getManeuverDrawable(node.mManeuverType));
            map.getMapView().getOverlays().add(nodeMarker);
        }

        map.getMapView().invalidate();
        binding.fabClearDirectionMapFragment.setVisibility(View.VISIBLE);
        binding.fabNavigate.setVisibility(View.VISIBLE);
    }

    public Drawable getManeuverDrawable(int maneuverType){
        switch (maneuverType){
            case 3:
                return getResources().getDrawable(R.drawable.ic_direction_turn_slight_left_3);
            case 4:
                return getResources().getDrawable(R.drawable.ic_direction_turn_left_4);
            case 6:
                return getResources().getDrawable(R.drawable.ic_direction_turn_slight_right_6);
            case 7:
                return getResources().getDrawable(R.drawable.ic_direction_turn_right_7);
            case 24:
                return getResources().getDrawable(R.drawable.ic_direction_start_end_24);
            default:
                return getResources().getDrawable(R.drawable.ic_direction_continue_24);
        }

    }


    public void onDirectionClearMarker(){
        Log.d(TAG, "onClearMarker: get number of overlays"+map.getMapView().getOverlays().size());

        for(int i=0;i<map.getMapView().getOverlays().size();i++){
            Overlay overlay= map.getMapView().getOverlays().get(i);

            if (overlay instanceof Polyline && ((Polyline) overlay).getId().equals("direction_overlay") ){
                map.getMapView().getOverlays().remove(overlay);
            }

            if(overlay instanceof Marker && ((Marker)overlay).getId().equals("direction_node") ){
                map.getMapView().getOverlays().remove(overlay);
                onDirectionClearMarker();
            }
        }
        //map.getMapView().invalidate();
        destination = null;
    }

    public void showSearchComponent(){
            binding.linearLayoutSearchView.setVisibility(View.VISIBLE);
    }
    public void hideSearchComponent(){
            binding.linearLayoutSearchView.setVisibility(View.GONE);
    }

    public void addEventListenerOverlay(){
        map.addEventListenerOverlay();
    }
    public void removeEventListenerOverlay(){
        map.removeEventListenerOverlay();
    }


    //set keyboard hiding functionality
    //it calls another function: hideKeyboard();
    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchClickListenerToRootView(){
        //View rootView = findViewById(android.R.id.root_container_mainActivity);
        View rootView = binding.rootViewMapFragment;
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
    }

    //implements the functionality of hiding keyboard upon clicking anywhere in the page
    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) ctx.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View currentFocus = getActivity().getCurrentFocus();
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    @Override
    public void onActivityResultLauncher(ImageView imageView) {
        chosenImageDialogBOx = imageView;
        mTakePhoto.launch("image/*");

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

    public void onReloadFragment(){
        map.setZoom(16f);
        map.setCenter(map.getMyLocationOverlay().getMyLocation());
    }

}