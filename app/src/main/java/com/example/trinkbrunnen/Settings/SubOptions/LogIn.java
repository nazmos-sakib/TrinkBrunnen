package com.example.trinkbrunnen.Settings.SubOptions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.trinkbrunnen.R;
import com.parse.ParseUser;

public class LogIn {
    View view;

    public LogIn(Context context, LinearLayout dynamicContent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService  (Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.bottom_sheet_settings_log_in,dynamicContent, false);
        init();
    }


    public View getView() {
        return view;
    }

    private void init(){


    }
}
