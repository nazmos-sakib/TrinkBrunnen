package com.example.trinkbrunnen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.Manifest;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;

import com.example.trinkbrunnen.Adaptar.BookmarksAdapter;
import com.example.trinkbrunnen.Callback.BookmarkReadyCallback;
import com.example.trinkbrunnen.Model.BookmarkLocationModel;
import com.example.trinkbrunnen.Model.DialogPlus;
import com.example.trinkbrunnen.Model.ParseQuarries;
import com.example.trinkbrunnen.fragments.Authentication.LoginFragment;
import com.example.trinkbrunnen.fragments.MapFragment;
import com.example.trinkbrunnen.Model.Map;
import com.example.trinkbrunnen.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BookmarkReadyCallback {
    private static final String TAG = "MainActivity->";
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private ActivityMainBinding binding;

    //list of fragments
    public static MapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //network operations on the main thread, disk read/write operations on the main thread,
        // and other costly operations that may impact the responsiveness of your application.
        StrictMode.ThreadPolicy policy = new StrictMode
                .ThreadPolicy
                .Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(policy);

        //cheek location permission
        //if permission granted continue with the activity
        askUserPermission();
    }


    private void initActivity(){

        try {
            this.getSupportActionBar().hide();
        } catch (Exception ignored){}

        // inflate layout file
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initBottomNavView();

        Map.getInstance(this);
        //Map.getInstance(this,findViewById(R.id.mapViewMapFragment));

        //fragments init
        mapFragment = new MapFragment(this);

        //load default map fragment
        replaceFragment(mapFragment);
        //replaceFragment(new test());

    }

    private void initBottomNavView(){
        Log.d(TAG, "initBottomNavView: started");
        //initial selected item
        binding.bottomNavigationViewMainActivity.setSelectedItemId(R.id.menu_items_map);
        //initial fragment
        //replaceFragment(new MapFragment(getApplicationContext()));

        binding.bottomNavigationViewMainActivity.setOnItemSelectedListener(item ->{
            switch (item.getItemId()){
                case R.id.menu_items_search:
                    //
                    //replaceFragment(new SearchFragment(MainActivity.this));
                    //replaceFragment(new MapFragment(MainActivity.this));
                    mapFragment.removeEventListenerOverlay();
                    mapFragment.showSearchComponent();
                    break;

                case R.id.menu_items_add:
                    //
                    mapFragment.hideSearchComponent();
                    mapFragment.addEventListenerOverlay();
                    //replaceFragment(new AddFragment(MainActivity.this));
                    break;
                case R.id.menu_items_map:
                    //
                    mapFragment.removeEventListenerOverlay();
                    mapFragment.hideSearchComponent();
                    replaceFragment(mapFragment);
                    break;

                case R.id.menu_items_save:
                    //
                    mapFragment.removeEventListenerOverlay();
                    mapFragment.hideSearchComponent();
                    if (ParseUser.getCurrentUser()!=null){
                        //this takes a callback function which will call shoeBookmarkBottomSheet
                        ParseQuarries.fetchBookMarkDataFromServer(this);
                    } else {
                        replaceFragment(new LoginFragment(MainActivity.mapFragment));
                        Snackbar.make(binding.getRoot(), "login to see bookmark", Snackbar.LENGTH_LONG).show();
                    }
                    break;

                case R.id.menu_items_settings:
                    //
                    mapFragment.removeEventListenerOverlay();
                    mapFragment.hideSearchComponent();
                    //replaceFragment(new SettingsFragment());
                    break;
                default:
                    break;
            }
            return true;
        });
    } //end of initBottomNavView()


    // Bookmark ----------------------------------------------------------
    private BookmarksAdapter bookmarksAdapter;
    //for showing bookmark dialogs
    @Override
    public void showBookmarkBottomSheet(ArrayList<BookmarkLocationModel> bookmarkLocationModelArrayList) {

        final DialogPlus dialog = new DialogPlus(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bookmark_bottom_sheet_layout);

        ProgressBar progressBar = dialog.findViewById(R.id.progressBar_bookmarkFragment);
        RecyclerView recView_bookmarksFragment = dialog.findViewById(R.id.recView_bookmarksFragment);

        bookmarksAdapter = new BookmarksAdapter(dialog);
        //get data from serve and set to the rec view holder
        bookmarksAdapter.setAdapterData(bookmarkLocationModelArrayList);
        progressBar.setVisibility(View.INVISIBLE);

        recView_bookmarksFragment.setAdapter(bookmarksAdapter);
        recView_bookmarksFragment.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //bookmarksAdapter.setAdapterData(fetchDataFromServer());
        Log.d(TAG, "setRecViewAdapter: "+bookmarksAdapter.getItemCount());

        //this should be the right place to call this function insted of onCreate method
        //fetchDataFromServer();
        Log.d(TAG, "setRecViewAdapter: "+bookmarksAdapter.getItemCount());

        //swipe implement
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recView_bookmarksFragment);


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.TOP);

    } //end of showBookMark()

    //bookmark swipe to delete functionality implementation
    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //Delete a Row--------------------------
            ParseQuarries.deleteBookmark(MainActivity.this,bookmarksAdapter.getBookmarkLocationArrayList().get(viewHolder.getAdapterPosition()),viewHolder);
        }
    };

    //after delete form database
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDeleteBookmark(RecyclerView.ViewHolder viewHolder) {
        //delete success
        bookmarksAdapter.getBookmarkLocationArrayList().remove(viewHolder.getAdapterPosition());
        bookmarksAdapter.notifyDataSetChanged();
        //soft acknowledgement
        Snackbar snackbar =  Snackbar.make(binding.getRoot(),"Item Deleted",Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
    // end of bookmark ----------------------------------------------------------

    //change fragment in fragment container
    private void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer_mainActivity,fragment)
                .addToBackStack(null)
                .commit();
    }


    private void askUserPermission(){
        // Check if the app has permission to access the user's location
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            //first time denied. ask again for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("We need permission for getting Current location")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                        REQUEST_LOCATION_PERMISSION);
                            }
                        })
                        .show();
            } else { //first time ask for permission
                // If the app doesn't have permission, request it from the user
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            }
        } else { //permission is already granted
            // initialize other component and functionality of this activity
            initActivity();
        }

    }

    // Override the onRequestPermissionsResult() method to handle the user's response
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with accessing the user's location
                // initialize other component and functionality of this activity
                initActivity();
            } else {
                // Permission denied, handle the error or inform the user
                startActivity(new Intent(MainActivity.this,PermissionDeniedActivity.class));
            }
        }
    }
}