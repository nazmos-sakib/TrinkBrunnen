package com.example.trinkbrunnen.Settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trinkbrunnen.Adaptar.SettingsContributionsAdapter;
import com.example.trinkbrunnen.Callback.BookmarkReadyCallback;
import com.example.trinkbrunnen.Model.DialogPlus;
import com.example.trinkbrunnen.Model.LocationModel;
import com.example.trinkbrunnen.Model.MapSingleton;
import com.example.trinkbrunnen.Model.ParseQuarries;
import com.example.trinkbrunnen.R;
import com.example.trinkbrunnen.Settings.SubOptions.LogIn;
import com.example.trinkbrunnen.Settings.SubOptions.MyFabPlaces;
import com.example.trinkbrunnen.Settings.SubOptions.SubOptionsDialog;
import com.example.trinkbrunnen.Settings.SubOptions.UserDetails;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseUser;

import org.osmdroid.api.IGeoPoint;

import java.util.ArrayList;

public class SettingsFirstPage extends Dialog {
    private static final String TAG = "SettingsFirstPage->";
    Context ctx;
    public SettingsFirstPage(@NonNull Context context) {
        super(context);

        this.ctx = context;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bottom_sheet_settings_first_page);


        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getWindow().setGravity(Gravity.BOTTOM);

        subOptions();
    }

    void subOptions(){

        //user details------------------------------------------------------------------------------------------
        // if user  logged in include user details layout else include log in layout
        // get ahold of the instance of your layout
        LinearLayout dynamicContent = (LinearLayout) findViewById(R.id.includeLayout_settingsFirstPage);
        View view;
        if (ParseUser.getCurrentUser()!=null){
            UserDetails usrD = new UserDetails(ctx,dynamicContent);
            //when log out button press, close SettingsFirstPage dialog
            usrD.init(
                    (E)->{
                        SettingsFirstPage.this.dismiss();
                    });
            view = usrD.getView();
        }else {
            LogIn logIn = new LogIn(ctx,dynamicContent);
            view = logIn.getView();
        }
        // add the inflated View to the layout
        dynamicContent.addView(view);



        //contributions------------------------------------------------------------------------------------------
        //onClicking the button it fetch  data from database.
        //the query function also take a callback function.
        // upon successfully acquiring data from server, it passes the data to "showSettingsContributionsBottomSheet()" function
        findViewById(R.id.tv_contribution_settingFragment).setOnClickListener(View->{
            if (ParseUser.getCurrentUser()!=null){
                ParseQuarries.fetchContributionsDataFromServer(locationArray -> showSettingsContributionsBottomSheet((ArrayList<LocationModel>) locationArray));
            }
        });

        //My favorite places------------------------------------------------------------------------------------------
        //show bookmarks
        findViewById(R.id.tv_favorite_place_settingFragment).setOnClickListener(View->{
            this.dismiss();

            //fetch bookmark data from server.
            //quarry function call "showBookMarkBottomSheet()" call-back function,
            //MyFabPlace Class takes Context, RecAdapter data(from server) and a dialog close listener.
            //The listener interface is for Dialog DismissListener
            //onBookmarkDialogDismiss show settings again
            if (ParseUser.getCurrentUser()!=null){
                //this takes a callback function which will call shoeBookmarkBottomSheet
                ParseQuarries.fetchBookMarkDataFromServer(
                        new BookmarkReadyCallback() {
                            @Override
                            public void showBookmarkBottomSheet(ArrayList<LocationModel> bookmarkLocationModelArrayList) {
                                //
                                new MyFabPlaces(ctx,bookmarkLocationModelArrayList,
                                         (dialogInterface)->{
                                     SettingsFirstPage.this.show();
                                 });
                            }

                            @Override
                            public void onDeleteBookmark(RecyclerView.ViewHolder viewHolder) {

                            }
                        }
                );
            } else {
                Snackbar.make(findViewById(R.id.parent_settingsFirstPage), "login to see bookmark", Snackbar.LENGTH_LONG).show();
            }

        });

        //Share my location------------------------------------------------------------------------------------------
        findViewById(R.id.tv_shareLocation_settingFragment).setOnClickListener(View->{

            try {
                 IGeoPoint point = MapSingleton.getInstance().getMapView().getMapCenter();
                String uri = "https://www.google.com/maps/search/?api=1&query=" +point.getLatitude()+","+point.getLongitude();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,"Check out the following location");
                intent.putExtra(Intent.EXTRA_TEXT,uri);
                ctx.startActivity(Intent.createChooser(intent,"Share Via"));
            } catch (Exception e) {
                e.printStackTrace();
            }



        });

        //Offline Maps
        findViewById(R.id.tv_offlineMap_settingFragment).setOnClickListener(View->{
            /*this.dismiss();
            SubOptionsDialog optionsDialog = new SubOptionsDialog(ctx, R.layout.bottom_sheet_settings_offline_map);
            //settings.show();

            optionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            //do some action here
                            SettingsFirstPage.this.show();
                        }
                    });*/

        });

        //check for updates
        findViewById(R.id.tv_checkForUpdate_settingFragment).setOnClickListener(View->{
            /*this.dismiss();
            SubOptionsDialog optionsDialog = new SubOptionsDialog(ctx, R.layout.bottom_sheet_settings_check_for_update);
            //settings.show();

            optionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            //do some action here
                            SettingsFirstPage.this.show();
                        }
                    });*/

        });


        //settings
        findViewById(R.id.tv_settings_settingFragment).setOnClickListener(View->{
            this.dismiss();
            SubOptionsDialog optionsDialog = new SubOptionsDialog(ctx, R.layout.bottom_sheet_settings_settings);

            optionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            //do some action here
                            SettingsFirstPage.this.show();
                        }
                    });

        });

        //privacy
        findViewById(R.id.tv_privacyManagement_settingFragment).setOnClickListener(View->{
            /*this.dismiss();
            SubOptionsDialog optionsDialog = new SubOptionsDialog(ctx, R.layout.bottom_sheet_settings_privacy);
            //settings.show();

            optionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    //do some action here
                    SettingsFirstPage.this.show();
                }
            });*/

        });

        //privacy
        findViewById(R.id.tv_aboutUs_settingFragment).setOnClickListener(View->{
            /*this.dismiss();
            SubOptionsDialog optionsDialog = new SubOptionsDialog(ctx, R.layout.bottom_sheet_settings_about_us);
            //settings.show();

            optionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    //do some action here
                    SettingsFirstPage.this.show();
                }
            });*/

        });

        //help
        findViewById(R.id.tv_help_settingFragment).setOnClickListener(View->{
            /*this.dismiss();
            SubOptionsDialog optionsDialog = new SubOptionsDialog(ctx, R.layout.bottom_sheet_settings_help);
            //settings.show();

            optionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    //do some action here
                    SettingsFirstPage.this.show();
                }
            });
*/
        });
    }


    //for SettingsContributions ---------------------------------------------
    //this function create Dialog that implements a recycle view and show fountain location added by the user
    public void showSettingsContributionsBottomSheet(ArrayList<LocationModel> locationArray){
        this.dismiss();
        Log.d(TAG, "showSettingsContributionsBottomSheet: "+locationArray.size());

        DialogPlus optionsDialog = new DialogPlus(ctx);
        optionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //start recycler view implementation
        optionsDialog.setContentView(R.layout.bottom_sheet_settings_contributions);
        RecyclerView recContributions = optionsDialog.findViewById(R.id.recView_settingsContribution);
        SettingsContributionsAdapter contributionsAdapter = new SettingsContributionsAdapter(optionsDialog,ctx);
        contributionsAdapter.setAdapterData(locationArray);

        recContributions.setAdapter(contributionsAdapter);
        recContributions.setLayoutManager(new LinearLayoutManager(ctx));
        //end



        optionsDialog.show();
        optionsDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        optionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        optionsDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        optionsDialog.getWindow().setGravity(Gravity.TOP);

        optionsDialog.findViewById(R.id.imageButton_close__settingsContribution).setOnClickListener(View->{
            optionsDialog.dismiss();
        });

        optionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //do some action here
                SettingsFirstPage.this.show();
            }
        });

    }

}
