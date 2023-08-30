package com.example.trinkbrunnen.Callback;

import androidx.recyclerview.widget.RecyclerView;

import com.example.trinkbrunnen.Model.LocationModel;

import java.util.ArrayList;

public interface BookmarkReadyCallback {
    void showBookmarkBottomSheet(ArrayList<LocationModel> bookmarkLocationModelArrayList);
    void onDeleteBookmark(RecyclerView.ViewHolder viewHolder);
}
