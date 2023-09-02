package com.example.oktravelapplictaion.model;



import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.HashMap;
import java.util.Map;

@Entity
public class User {

    final public static String COLLECTIONNAME = "users";
    final public static String USERNAME = "userName";
    final public static String PASSWORD = "password";
    final public static String EMAIL = "email";
    final public static String PHONENUMBER = "phoneNumber";
    final public static String IMAGEURL = "imageUrl";
    final public static String LIKEDLIST = "likedList";

    @NonNull
    String userName;

    @NonNull
    String password;

    @NonNull
    @PrimaryKey
    String email;

    @NonNull
    String phoneNumber;

    @NonNull
    String imageUrl;
    String likedList;

    public User(){}

    public User(String userName, String password, String email, String phoneNumber, String imageUrl) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setProfileImageUrl(String url) {
        this.imageUrl = url;
    }

    public String getLikedList() {
        return likedList;
    }

    public void setLikedList(String likedList) {
        this.likedList = likedList;
    }

    public HashMap<String, Object> toJson() {
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put(USERNAME, userName);
        json.put(PASSWORD, password);
        json.put(EMAIL, email);
        json.put(PHONENUMBER, phoneNumber);
        json.put(IMAGEURL, imageUrl);

        return json;
    }

    public static User create(Map<String, Object> json) {
        String userName = (String) json.get(USERNAME);
        String password = (String) json.get(PASSWORD);
        String email = (String) json.get(EMAIL);
        String phoneNumber = (String) json.get(PHONENUMBER);
        String imageUrl = (String) json.get(IMAGEURL);
        String likedList = (String) json.get(LIKEDLIST);
        User user = new User (userName, password,email, phoneNumber, imageUrl);
        user.setLikedList(likedList);
        return user;
    }
}
