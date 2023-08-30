package com.example.trinkbrunnen.Adaptar;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trinkbrunnen.Model.LocationModel;
import com.example.trinkbrunnen.Callback.RecyclerViewClickListener;
import com.example.trinkbrunnen.databinding.BookmarkRecycleViewBinding;

import java.util.ArrayList;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.ViewHolder>{
    private static final String TAG = "BookmarksAdapter->";

    private ArrayList<LocationModel> bookmarkLocationArrayList;
    private RecyclerViewClickListener recyclerViewClickListener;

    public BookmarksAdapter(RecyclerViewClickListener recyclerViewClickListener) {
        this.recyclerViewClickListener = recyclerViewClickListener;
        bookmarkLocationArrayList  = new ArrayList<>();
    }

    public BookmarksAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BookmarkRecycleViewBinding view = BookmarkRecycleViewBinding.inflate( LayoutInflater.from(parent.getContext()), parent,false);

        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tv_name.setText(bookmarkLocationArrayList.get(position).getLocationName());
        holder.tv_details.setText(bookmarkLocationArrayList.get(position).getCreatedAt());

        int index = position;

        //setting click listener
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //passing the clicked position to the interface
                //bookmarkLocationArrayList.get(index).getGeoPoint()
                recyclerViewClickListener.onRecViewItemClick(bookmarkLocationArrayList.get(index));
            }
        });
    }



    //getting clicked data
    public LocationModel getItemData(int position) {
        return bookmarkLocationArrayList.get(position);
    }

    //updating the data of the recView
    @SuppressLint("NotifyDataSetChanged")
    public void setAdapterData(ArrayList<LocationModel> bookmarkLocations) {
        Log.d(TAG, "setAdapterData: called. size-> "+bookmarkLocations.size());
        this.bookmarkLocationArrayList = bookmarkLocations;
        notifyDataSetChanged();
    }


    public ArrayList<LocationModel> getBookmarkLocationArrayList() {
        return bookmarkLocationArrayList;
    }

    @Override
    public int getItemCount() {
        return bookmarkLocationArrayList.size();
    }

    public static class  ViewHolder extends RecyclerView.ViewHolder{


        private TextView tv_name,tv_details;
        private LinearLayout parent;

        public ViewHolder(@NonNull BookmarkRecycleViewBinding itemView){
            super(itemView.getRoot());
            /*tv_name = itemView.findViewById(R.id.tv_name_bookmarkRecView);
            parent = itemView.findViewById(R.id.parent_bookmarkRecView);*/
            //binding = itemView;

            tv_name = itemView.tvNameBookmarkRecView;
            tv_details = itemView.tvDetailsBookmarkRecView;
            parent = itemView.parentBookmarkRecView;
        }
    }

}
