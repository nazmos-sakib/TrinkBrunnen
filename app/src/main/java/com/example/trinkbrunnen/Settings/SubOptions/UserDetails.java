package com.example.trinkbrunnen.Settings.SubOptions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.trinkbrunnen.Callback.Callback;
import com.example.trinkbrunnen.R;
import com.parse.ParseUser;

public class UserDetails {
    View view;

    public UserDetails(Context context, LinearLayout dynamicContent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService  (Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.bottom_sheet_settings_user_details,dynamicContent, false);
        //init();
    }


    public View getView() {
        return view;
    }

    public void init(Callback callback){
        TextView email = view.findViewById(R.id.tv_email_userDetails);
        email.setText(ParseUser.getCurrentUser().getEmail());

        TextView name = view.findViewById(R.id.tv_name_userDetails);
        name.setText(ParseUser.getCurrentUser().getUsername());

        view.findViewById(R.id.btn_logOut_userDetails).setOnClickListener(View->{
            ParseUser.logOut();
            callback.onCallback(null);
        });

    }
}
