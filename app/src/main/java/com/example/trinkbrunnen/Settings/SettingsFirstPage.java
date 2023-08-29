package com.example.trinkbrunnen.Settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;

import com.example.trinkbrunnen.Callback.DialogCallback;
import com.example.trinkbrunnen.R;

public class SettingsFirstPage extends Dialog {
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
        findViewById(R.id.tv_settings_settingFragment).setOnClickListener(View->{

            this.dismiss();
            Settings settings = new Settings(ctx);
            settings.show();

            settings.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            //do some action here
                            settings.onDismissCallback(SettingsFirstPage.this::show);
                        }
                    });

        });
    }

}
