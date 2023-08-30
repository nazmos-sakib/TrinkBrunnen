package com.example.trinkbrunnen.Adaptar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trinkbrunnen.Callback.RecyclerViewClickListener;
import com.example.trinkbrunnen.Model.LocationModel;
import com.example.trinkbrunnen.R;
import com.example.trinkbrunnen.databinding.BottomSheetSettingsContributionsRecViewCardBinding;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.ArrayList;

public class SettingsContributionsAdapter extends RecyclerView.Adapter<SettingsContributionsAdapter.ViewHolder> {
    private static final String TAG = "SettingContributionsAdapter->";

    Context ctx;
    private ArrayList<LocationModel> fountainLocations;
    private RecyclerViewClickListener recyclerViewClickListener;

    public SettingsContributionsAdapter(RecyclerViewClickListener recyclerViewClickListener,Context ctx) {
        this.recyclerViewClickListener = recyclerViewClickListener;
        this.ctx = ctx;
        fountainLocations = new ArrayList<>();
    }

    public SettingsContributionsAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BottomSheetSettingsContributionsRecViewCardBinding view = BottomSheetSettingsContributionsRecViewCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent,false);
        //TestLayoutBinding view = TestLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: "+fountainLocations.get(position).toString());

        holder.title.setText(fountainLocations.get(position).getLocationName());
        holder.description.setText(fountainLocations.get(position).getDetails());
        holder.isActive.setText(String.format("Is it currently active: %s", fountainLocations.get(position).isActive()));

        ParseFile f = fountainLocations.get(position).getImage();

        if (f!=null){
            f.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        // The image data was successfully retrieved
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        // Use the bitmap as needed
                        Drawable drawable = new BitmapDrawable(ctx.getResources(), bitmap);

                        holder.fountainImg.setImageDrawable(drawable);

                    } else {
                        // There was an error retrieving the image data
                        Log.e(TAG, "Error loading image data: " + e.getMessage());
                    }
                }
            });
        }

        holder.edit.setOnClickListener(View->{
            //TODO
        });
    }


    //getting clicked data
    public LocationModel getItemData(int position) {
        return fountainLocations.get(position);
    }

    //updating the data of the recView
    @SuppressLint("NotifyDataSetChanged")
    public void setAdapterData(ArrayList<LocationModel> locations) {
        Log.d(TAG, "setAdapterData: called. size-> "+fountainLocations.size());
        this.fountainLocations = locations;
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return fountainLocations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView fountainImg;
        TextView title, edit, description, isActive;

        public ViewHolder(BottomSheetSettingsContributionsRecViewCardBinding view) {
            super(view.getRoot());
            fountainImg = view.imgFountainSettingsContributionsCardView;
            title = view.tvTitleSettingsContributionsCardView;
            description = view.tvDescriptionSettingsContributionsCardView;
            isActive = view.tvIsActiveSettingsContributionsCardView;

            edit = view.tvEditSettingsContributionsCardView;
        }
    }

/*
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(TestLayoutBinding view) {
            super(view.getRoot());

        }
    }*/
}
