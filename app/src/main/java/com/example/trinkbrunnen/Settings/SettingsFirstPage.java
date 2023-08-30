package com.example.trinkbrunnen.Settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trinkbrunnen.Adaptar.SettingsContributionsAdapter;
import com.example.trinkbrunnen.Model.DialogPlus;
import com.example.trinkbrunnen.Model.LocationModel;
import com.example.trinkbrunnen.Model.ParseQuarries;
import com.example.trinkbrunnen.R;
import com.example.trinkbrunnen.Settings.SubOptions.SubOptionsDialog;

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

        //contributions
        findViewById(R.id.tv_contribution_settingFragment).setOnClickListener(View->{
            ParseQuarries.fetchContributionsDataFromServer(locationArray -> showSettingsContributionsBottomSheet((ArrayList<LocationModel>) locationArray));
        });

        //My favorite places
        findViewById(R.id.tv_favorite_place_settingFragment).setOnClickListener(View->{
            this.dismiss();
            SubOptionsDialog optionsDialog = new SubOptionsDialog(ctx, R.layout.bottom_sheet_settings_my_fav_place);
            //settings.show();

            optionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            //do some action here
                            SettingsFirstPage.this.show();
                        }
                    });

        });

        //Share my location
        findViewById(R.id.tv_shareLocation_settingFragment).setOnClickListener(View->{
            this.dismiss();
            SubOptionsDialog optionsDialog = new SubOptionsDialog(ctx, R.layout.bottom_sheet_settings_share_my_location);
            //settings.show();

            optionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            //do some action here
                            SettingsFirstPage.this.show();
                        }
                    });

        });

        //Offline Maps
        findViewById(R.id.tv_offlineMap_settingFragment).setOnClickListener(View->{
            this.dismiss();
            SubOptionsDialog optionsDialog = new SubOptionsDialog(ctx, R.layout.bottom_sheet_settings_offline_map);
            //settings.show();

            optionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            //do some action here
                            SettingsFirstPage.this.show();
                        }
                    });

        });

        //check for updates
        findViewById(R.id.tv_checkForUpdate_settingFragment).setOnClickListener(View->{
            this.dismiss();
            SubOptionsDialog optionsDialog = new SubOptionsDialog(ctx, R.layout.bottom_sheet_settings_check_for_update);
            //settings.show();

            optionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            //do some action here
                            SettingsFirstPage.this.show();
                        }
                    });

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
            this.dismiss();
            SubOptionsDialog optionsDialog = new SubOptionsDialog(ctx, R.layout.bottom_sheet_settings_privacy);
            //settings.show();

            optionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    //do some action here
                    SettingsFirstPage.this.show();
                }
            });

        });

        //privacy
        findViewById(R.id.tv_aboutUs_settingFragment).setOnClickListener(View->{
            this.dismiss();
            SubOptionsDialog optionsDialog = new SubOptionsDialog(ctx, R.layout.bottom_sheet_settings_about_us);
            //settings.show();

            optionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    //do some action here
                    SettingsFirstPage.this.show();
                }
            });

        });
        //privacy
        findViewById(R.id.tv_help_settingFragment).setOnClickListener(View->{
            this.dismiss();
            SubOptionsDialog optionsDialog = new SubOptionsDialog(ctx, R.layout.bottom_sheet_settings_help);
            //settings.show();

            optionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    //do some action here
                    SettingsFirstPage.this.show();
                }
            });

        });
    }
    //for SettingsContributions ---------------------------------------------

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
