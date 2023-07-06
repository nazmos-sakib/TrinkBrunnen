package com.example.trinkbrunnen.Callback;

import androidx.recyclerview.widget.RecyclerView;

import com.example.trinkbrunnen.Model.BookmarkLocationModel;

import java.util.ArrayList;

public interface BookmarkReadyCallback {
    void showBookmarkBottomSheet(ArrayList<BookmarkLocationModel> bookmarkLocationModelArrayList);
    void onDeleteBookmark(RecyclerView.ViewHolder viewHolder);
}
