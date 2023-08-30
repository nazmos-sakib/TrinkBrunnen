package com.example.trinkbrunnen.Model;

import com.parse.ParseFile;

import org.osmdroid.util.GeoPoint;

public class LocationModel {
    private String id;
    private String userId;
    private String details;
    private String locationName;
    private String createdAt;
    private GeoPoint geoPoint;
    private boolean isActive;
    ParseFile image;

    public LocationModel() {
    }


    public LocationModel(String id, String userId, String locationName, String createdAt, GeoPoint geoPoint) {
        this.id = id;
        this.userId = userId;
        this.locationName = locationName;
        this.createdAt = createdAt;
        this.geoPoint = geoPoint;

    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocationModel(String id, String userId, String locationName, String details, boolean isActive, String createdAt, GeoPoint geoPoint,ParseFile markerImage) {
        this.id = id;
        this.userId = userId;
        this.locationName = locationName;
        this.details = details;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.geoPoint = geoPoint;
        this.image = markerImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public ParseFile getImage() {
        return image;
    }

    public void setImage(ParseFile image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "LocationModel{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", details='" + details + '\'' +
                ", locationName='" + locationName + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", geoPoint=" + geoPoint +
                ", isActive=" + isActive +
                '}';
    }
}
