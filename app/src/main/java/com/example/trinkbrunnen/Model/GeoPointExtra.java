package com.example.trinkbrunnen.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.osmdroid.util.GeoPoint;

public class GeoPointExtra implements Parcelable {
    private GeoPoint location;
    private double latitude, longitude;


    public GeoPointExtra(GeoPoint location) {
        this.location = location;
    }

    protected GeoPointExtra(Parcel in) {
    }

    public static final Creator<GeoPointExtra> CREATOR = new Creator<GeoPointExtra>() {
        @Override
        public GeoPointExtra createFromParcel(Parcel in) {
            return new GeoPointExtra(in);
        }

        @Override
        public GeoPointExtra[] newArray(int size) {
            return new GeoPointExtra[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

    public GeoPoint getLocation() {
        return location;
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }


}
