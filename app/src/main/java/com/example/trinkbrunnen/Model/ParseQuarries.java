package com.example.trinkbrunnen.Model;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.example.trinkbrunnen.Callback.BookmarkReadyCallback;
import com.example.trinkbrunnen.Callback.Callback;
import com.example.trinkbrunnen.Callback.FountainLocationCallback;
import com.example.trinkbrunnen.Callback.UploadingBookmarkFinishCallback;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class ParseQuarries {

    private static final String TAG = "ParseQuarries->";

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
        ArrayList<LocationModel> arrayList = new ArrayList<>();
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
                            LocationModel b = new LocationModel(
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
    public static void fetchContributionsDataFromServer(Callback callback){
        ArrayList<LocationModel> arrayList = new ArrayList<>();
        ParseUser currentUser = ParseUser.getCurrentUser();
        //SELECT * FROM 'TABLE' WHERE 'COLUMN' = 'VALUE' LIMIT 1 ; --implementation
        ParseQuery<ParseObject> query1  = new ParseQuery<ParseObject>("fountainLocation");
        query1.orderByDescending("createdAt");
        query1.whereEqualTo("userWhoAddedThis",currentUser.getObjectId());
        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    Log.d(TAG, "done: "+objects.size());
                    if (objects.size()>0){
                        for (ParseObject object:objects){
                            //Log.d(TAG, "done: "+object.toString());
                            LocationModel b = new LocationModel(
                                    object.getObjectId(),
                                    object.getString("userWhoAddedThis"),
                                    object.getString("title"),
                                    object.getString("description"),
                                    object.getBoolean("isCurrentlyActive"),
                                    object.getCreatedAt().toString(),
                                    new GeoPoint(
                                            object.getParseGeoPoint("location").getLatitude(),
                                            object.getParseGeoPoint("location").getLongitude()
                                    ),
                                    object.getParseFile("image")
                            );
                            arrayList.add(b);
                        }

                        callback.onCallback((ArrayList<LocationModel>) arrayList);

                    }
                }

            }
        });

    }

    public static void uploadNewBookmark(UploadingBookmarkFinishCallback callback,String title, GeoPoint location){
        //Score is the table name. if 'Score' does not exist then its create one.
        ParseObject score = new ParseObject("Bookmarks");
        score.put("title",title); //Column name and value
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
    public static void deleteBookmark(BookmarkReadyCallback callback, LocationModel deletedObj, RecyclerView.ViewHolder viewHolder){
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

    public static void appendNewFountainLocation(String title, String description, GeoPoint location,boolean isActive,byte[] data, Callback callback){
        ParseObject obj = new ParseObject("fountainLocation");
        //ParseObject obj = new ParseObject("asd");
        Log.d(TAG, "appendNewFountainLocation: ->adding to server");
        obj.put("title",title);

        obj.put("description",description);
        obj.put("location",  new ParseGeoPoint(location.getLatitude(),location.getLongitude()));
        obj.put("userWhoAddedThis", ParseUser.getCurrentUser().getObjectId());
        obj.put("isCurrentlyActive",isActive);

        if (null!=data){
            ParseFile parseFile = new ParseFile("image-", data);
            obj.put("image", parseFile);
        }

        obj.saveInBackground(e->{
            if (e==null) { // no error occurred
                Log.d(TAG, "parse server upload->done: -> Succeed");
                callback.onCallback(null);
            } else {
                e.getMessage();
                e.getStackTrace();
            }
        });
    }

    public static void alterFountainData(String id,String title, String description, boolean isActive,byte[] data, Callback callback){

        String objectID = id.split("\\+")[1];
        Log.d(TAG, "alterFountainData: "+objectID);

        //get the whole row of the data
        ParseQuery<ParseObject> queryU  = new ParseQuery<ParseObject>("fountainLocation");
        queryU.getInBackground(objectID, (obj, e) -> {
            if (e == null) {
                obj.put("title",title);

                obj.put("description",description);
                obj.put("userWhoUpdatedThis", ParseUser.getCurrentUser().getObjectId());
                obj.put("isCurrentlyActive",isActive);

                if (null!=data){
                    ParseFile parseFile = new ParseFile("image-", data);
                    obj.put("image", parseFile);
                }
                obj.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null){
                            //update success
                            callback.onCallback(null);
                        } else {
                            e.getStackTrace();
                        }
                    }
                });
            } else {

            }
        });

    }
}
