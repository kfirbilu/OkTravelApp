package com.example.oktravelapplictaion.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Location {
    public static final String COLLECTIONNAME = "Locations";
    private static final String POSTID = "postId";
    private static final String LONGITUDE = "longitude";
    private static final String LOCATIONNAME = "locationName";
    private static final String LATITUDE = "latitude";
    private static final String ISDELETED = "is_deleted";
    @PrimaryKey
    @NonNull
    String postId;
    @NonNull
    String locationName;
    @NonNull
    float longitude;
    @NonNull
    float latitude;
    boolean is_deleted;

    public Location(@NonNull String postId, @NonNull String locationName, float longitude, float latitude) {
        this.postId = postId;
        this.locationName = locationName;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    @NonNull
    public String getPostId() {
        return postId;
    }

    public void setPostId(@NonNull String postId) {
        this.postId = postId;
    }

    @NonNull
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(@NonNull String locationName) {
        this.locationName = locationName;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put(POSTID, postId);
        json.put(LOCATIONNAME, locationName);
        json.put(LONGITUDE, longitude);
        json.put(LATITUDE, latitude);
        json.put(ISDELETED,is_deleted);
        return json;
    }

    public static Location create(Map<String, Object> json) {
        String postId = (String) json.get(POSTID);
        String locationName = (String) json.get(LOCATIONNAME);
        float longitude = Float.parseFloat(json.get(LONGITUDE).toString());
        float latitude = Float.parseFloat(json.get(LATITUDE).toString());
        boolean is_deleted=(boolean)json.get(ISDELETED);
        Location location = new Location (postId,locationName,longitude,latitude);
        location.setIs_deleted(is_deleted);

        return location;
    }
}
