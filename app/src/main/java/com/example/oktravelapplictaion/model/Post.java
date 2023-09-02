package com.example.oktravelapplictaion.model;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Post {
    final public static String TITLE = "postTitle";
    final public static String VIDEOURL = "videoUrl";
    public static final String COLLECTIONNAME = "posts";
    private static final String DESCRIPTION = "postDescription";
    private static final String LIKES = "likes";
    private static final String USERNAME = "userName";
    private static final String CATEGORIESLIST = "categoriesList";
    public static final String UPDATEDATE = "updateDate";
    private static final String POSTID ="postID" ;
    private static final String ISDELETED = "isDeleted";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";

    @NonNull
    @PrimaryKey
    String postID;
    @NonNull
    String postTitle;
    String postDescription;
    int likes;
    @NonNull
    String userName;
    String categoriesList;
    @NonNull
    String videoUrl;
    boolean isDeleted;
    float longitude;
    float latitude;
    Long updateDate=new Long(0);
    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }



    public Post(String postTitle, String postDescription, String userName,int likes, String categoriesList, String videoUrl, float longitude,float latitude){
        this.postTitle = postTitle;
        this.likes=likes;
        this.postDescription = postDescription;
        this.userName = userName;
        this.categoriesList = categoriesList;
        this.videoUrl = videoUrl;
        this.isDeleted=false;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    @NonNull
    public String getUserName() {
        return userName;
    }

    public void setUserName(@NonNull String userName) {
        this.userName = userName;
    }

    public String getCategoriesList() {
        return categoriesList;
    }

    public void setCategoriesList(String categoriesList) {
        this.categoriesList = categoriesList;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public HashMap<String, Object> toJson() {
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put(TITLE, postTitle);
        json.put(DESCRIPTION, postDescription);
        json.put(LIKES, String.valueOf(likes));
        json.put(USERNAME,userName);
        json.put(CATEGORIESLIST,categoriesList);
        json.put(VIDEOURL, videoUrl);
        json.put(POSTID,postID);
        json.put(ISDELETED,isDeleted);
        json.put(LONGITUDE,String.valueOf(longitude));
        json.put(LATITUDE,String.valueOf(latitude));
        return json;
    }

    public static Post create(Map<String, Object> json) {
        String postTitle = (String) json.get(TITLE);
        String description = (String)json.get(DESCRIPTION);
        int likes = Integer.parseInt((String)json.get(LIKES));
        String userName= (String)json.get(USERNAME);
        String categoriesList = (String) json.get(CATEGORIESLIST);
        String videoUrl = (String) json.get(VIDEOURL);
        Timestamp ts = (Timestamp) json.get(UPDATEDATE);
        Long updateDate = ts.getSeconds();
        float longitude = Float.parseFloat(json.get(LONGITUDE).toString());
        float latitude = Float.parseFloat(json.get(LATITUDE).toString()) ;

        String postID = (String) json.get(POSTID);
        boolean isDeleted = (boolean)json.get(ISDELETED);
        Post post = new Post (postTitle,description,userName,likes,categoriesList,videoUrl,longitude,latitude);
        post.setPostID(postID);
        post.setUpdateDate(updateDate);
        post.setDeleted(isDeleted);
        return post;
    }

    public Long getUpdateDate() {
        return updateDate;
    }
}
