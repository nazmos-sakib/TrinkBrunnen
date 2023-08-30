package com.example.trinkbrunnen.Settings.SubOptions;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;

import com.example.trinkbrunnen.Callback.DialogCallback;
import com.example.trinkbrunnen.R;

public class Settings extends Dialog {
    Context ctx;

    public Settings(@NonNull Context context) {
        super(context);
        this.ctx = context;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bottom_sheet_settings_settings);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getWindow().setGravity(Gravity.BOTTOM);

        show();

    }

    void onDismissCallback(DialogCallback callback){
        callback.onDialogCallback();
    }

}
