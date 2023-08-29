package com.example.trinkbrunnen.Model;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.example.trinkbrunnen.Callback.BookmarkReadyCallback;
import com.example.trinkbrunnen.Callback.FountainLocationCallback;
import com.example.trinkbrunnen.Callback.UploadingBookmarkFinishCallback;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class ParseQuarries {

    //
    public static void fetchFountainLocation(GeoPoint location,FountainLocationCallback callback){
        ParseQuery<ParseObject> query = new ParseQuery<>("fountainLocation");
        query.whereWithinKilometers("location",new ParseGeoPoint(location.getLatitude(),location.getLongitude()),30);
        query.findInBackground((objects, e) -> {
            if (e==null){
                callback.onFountainLocationFound(objects);
            } else {
                Log.d("TAG->", "getNearByFountainLocation->: null value");
                //Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void fetchBookMarkDataFromServer(BookmarkReadyCallback callback){
        ArrayList<BookmarkLocationModel> arrayList = new ArrayList<>();
        ParseUser currentUser = ParseUser.getCurrentUser();
        //SELECT * FROM 'TABLE' WHERE 'COLUMN' = 'VALUE' LIMIT 1 ; --implementation
        ParseQuery<ParseObject> query1  = new ParseQuery<ParseObject>("Bookmarks");
        query1.orderByDescending("createdAt");
        query1.whereEqualTo("user_id",currentUser.getObjectId());
        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    if (objects.size()>0){
                        for (ParseObject object:objects){
                            //Log.d(TAG, "done: "+object.toString());
                            BookmarkLocationModel b = new BookmarkLocationModel(
                                    object.getObjectId(),
                                    object.getString("user_id"),
                                    object.getString("title"),
                                    object.getCreatedAt().toString(),
                                    new GeoPoint(
                                            object.getParseGeoPoint("location").getLatitude(),
                                            object.getParseGeoPoint("location").getLongitude()
                                    )
                            );
                            arrayList.add(b);
                        }

                        callback.showBookmarkBottomSheet(arrayList);

                    }
                }

            }
        });

    }

    public static void uploadNewBookmark(UploadingBookmarkFinishCallback callback, GeoPoint location){
        //Score is the table name. if 'Score' does not exist then its create one.
        ParseObject score = new ParseObject("Bookmarks");
        score.put("user_id",ParseUser.getCurrentUser().getObjectId()); //Column name and value
        score.put("location", new ParseGeoPoint(location.getLatitude(),location.getLongitude()));
        score.saveInBackground(new SaveCallback() { //upload file to the parse server
            //using this call back function will return extra information like if it failed or succeed to upload the file
            @Override
            public void done(ParseException e) {
                if (e==null) { // no error occurred
                    callback.onBookmarkUploadFinish();
                } else {
                    e.getMessage();
                    e.getStackTrace();
                }
            }
        });

    }

    //Delete a Row--------------------------
    public static void deleteBookmark(BookmarkReadyCallback callback, BookmarkLocationModel deletedObj, RecyclerView.ViewHolder viewHolder){
        ParseQuery<ParseObject> queryD = ParseQuery.getQuery("Bookmarks");
        queryD.getInBackground(deletedObj.getId(), new GetCallback<ParseObject>() { //objectId is the primary key
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e==null && object != null) {
                    object.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e==null){
                                //delete success
                                callback.onDeleteBookmark(viewHolder);
                            } else {
                                e.getMessage();
                            }
                        }
                    });
                }
            }
        });

    }
}
