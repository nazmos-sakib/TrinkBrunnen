package com.example.trinkbrunnen.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.trinkbrunnen.BuildConfig;
import com.example.trinkbrunnen.Callback.LocationLoadedCallback;
import com.example.trinkbrunnen.Callback.StartActivityForResultCallback;
import com.example.trinkbrunnen.Callback.UploadingBookmarkFinishCallback;
import com.example.trinkbrunnen.MapboxTestNavigation;
import com.example.trinkbrunnen.Model.CustomInfoWindow;
import com.example.trinkbrunnen.Model.GeoPointExtra;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment implements LocationLoadedCallback, UploadingBookmarkFinishCallback, StartActivityForResultCallback {
    private static final String TAG = "MapFragment->";

    FragmentMapBinding binding;
    Context ctx;

    Map map;
    GeoPoint destination = null;
    public static GeoPoint mapCameraPosition=null;

    AlertDialog alertDialog;

    //addToServer dialog box image view global variable
    ImageView chosenImageDialogBOx;
    //startActivityForResult
    ActivityResultLauncher<String> mTakePhoto ;


    public MapFragment(Context ctx) {
        // Required empty public constructor
        this.ctx = ctx;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Configuration.getInstance().load(this.ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //Adjust the size of the cache on disk The primary usage is downloaded map tiles
        //this will set the disk cache size in MB to 1GB , 9GB trim size
        //OpenStreetMapTileProviderConstants. (1000L, 900L);
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
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

        if (mapCameraPosition==null){
            map.setCenter(map.getMyLocationOverlay().getMyLocation());
        } else {
            map.setCenter(mapCameraPosition);
        }


        //map.addZoomScrollListener();

        //binding.map.addOnFirstLayoutListener();


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
    }



    private void setFABClickListener(){
        //binding.fabStandPositionMainActivity.setBackgroundDrawable(ContextCompat.getDrawable(ctx, R.drawable.floating_button_bg));

        //current position. on click set map camera to users current location
        binding.fabStandPositionMainActivity.setOnClickListener(View -> {
            map.setZoom(16f);
            map.setCenter(map.getMyLocationOverlay().getMyLocation());
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
                            return true;
                        case R.id.menu_layout_satellite:
                            // Do something when option 2 is clicked
                            map.setUSGS_SATTileSource();
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
    }



    @Override
    public void onLocationLoaded(GeoPoint location) {

        // get the bounding box of the visible area
        BoundingBox boundingBox = map.getMapView().getBoundingBox();

        // calculate the visible area in square kilometers
        double area = boundingBox.getLatitudeSpan() * boundingBox.getLongitudeSpan() * Math.cos(Math.toRadians(boundingBox.getCenter().getLatitude())) * 111.319;
        Log.d(TAG, "getNearByFountainLocation: ->calculating "+area);

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
                        m.setInfoWindow(new CustomInfoWindow(map.getMapView(),
                                ()->{
                                    showDialogOnMarkerLongClick(m);
                                }
                                        ));


                    }



                });

    }


    //on marker single click call this function
    private void showDialogOnMarkerLongClick(Marker m) {
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
        View view = inflater.inflate(R.layout.map_fragment_direction_bookmark_clear_dialog, null);

        // Set the view to the AlertDialog
        alertDialog.setView(view);

        Button directionBtn = view.findViewById(R.id.btn_addToServer_map_dialog);
        directionBtn.setOnClickListener(View->{
            createDirection(m);

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
            ParseQuarries.uploadNewBookmark(MapFragment.this,m.getPosition());
            //alertDialog.dismiss();
        });

        alertDialog.show();
    } //end of showDialogOnMarkerLongClick()


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
        //binding.map.invalidate();
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
}