package com.example.trinkbrunnen.Settings.SubOptions;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trinkbrunnen.Adaptar.BookmarksAdapter;
import com.example.trinkbrunnen.Callback.BookmarkReadyCallback;
import com.example.trinkbrunnen.Model.DialogPlus;
import com.example.trinkbrunnen.Model.LocationModel;
import com.example.trinkbrunnen.Model.ParseQuarries;
import com.example.trinkbrunnen.R;

import java.util.ArrayList;

public class MyFabPlaces {
    private static final String TAG = "MyFabPlaces->";
    private BookmarksAdapter bookmarksAdapter;

    public MyFabPlaces(Context context, ArrayList<LocationModel> bookmarkLocationModelArrayList, DialogInterface.OnDismissListener listener){

        final DialogPlus dialog = new DialogPlus(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bookmark_bottom_sheet_layout);

        ProgressBar progressBar = dialog.findViewById(R.id.progressBar_bookmarkFragment);
        RecyclerView recView_bookmarksFragment = dialog.findViewById(R.id.recView_bookmarksFragment);

        bookmarksAdapter = new BookmarksAdapter(dialog);
        //get data from serve and set to the rec view holder
        bookmarksAdapter.setAdapterData(bookmarkLocationModelArrayList);
        progressBar.setVisibility(View.INVISIBLE);

        recView_bookmarksFragment.setAdapter(bookmarksAdapter);
        recView_bookmarksFragment.setLayoutManager(new LinearLayoutManager(context));
        //bookmarksAdapter.setAdapterData(fetchDataFromServer());
        Log.d(TAG, "setRecViewAdapter: "+bookmarksAdapter.getItemCount());

        //this should be the right place to call this function insted of onCreate method
        //fetchDataFromServer();
        Log.d(TAG, "setRecViewAdapter: "+bookmarksAdapter.getItemCount());

        //swipe implement
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recView_bookmarksFragment);


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.TOP);

        //dialog.setOnDismissListener(listener);
        //dialog.onBackPressed();

    }

    //bookmark swipe to delete functionality implementation
    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //Delete a Row--------------------------
            ParseQuarries.deleteBookmark(new BookmarkReadyCallback() {
                @Override
                public void showBookmarkBottomSheet(ArrayList<LocationModel> bookmarkLocationModelArrayList) {

                }

                @Override
                public void onDeleteBookmark(RecyclerView.ViewHolder viewHolder) {

                }
            }, bookmarksAdapter.getBookmarkLocationArrayList().get(viewHolder.getAdapterPosition()), viewHolder);
        }
    };
}
