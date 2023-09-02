package com.example.oktravelapplictaion.login;

import com.example.oktravelapplictaion.model.Location;
import com.example.oktravelapplictaion.model.Post;
import com.example.oktravelapplictaion.model.User;

import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface RetrofitInterface {

    @POST("/auth/login")
    Call<LoginResult> executeLogin(@Body HashMap<String,String> map);
    @POST("/auth/loginApps")
    Call<LoginResult> loginApps(@Body HashMap<String,String> map);

    @POST("/auth/register")
    Call<Void> executeSignup(@Body HashMap<String,Object> map);

    @POST("user/findByUserName")
    Call<LoginResult> findByUserName(@Body HashMap<String,String> map);

    @POST("user/editUser")
    Call<Void> editUser(@Body HashMap<String,String> map);

    @POST("post/editPost")
    Call<Void> editPost(@Body HashMap<String,Object> map, @Header("authorization") String access_token);

    @POST("/post/addNewPost")
    Call<Void> addNewPost(@Body HashMap<String,Object> map, @Header("authorization") String access_token);

    @POST("/post/getAllPosts")
    Call<List<Post>> getAllPosts( @Body HashMap<String,String> map);

    @POST("/post/getPostsbyUsername")
    Call<List<Post>> getPostsbyUsername( @Body HashMap<String,String> map);

    @POST("/post/deletePost")
    Call<Void> deletePost( @Body HashMap<String,String> map,@Header("authorization") String access_token);

    @GET("/location/getAllLocations")
    Call<List<Location>> getAllLocations();

    @POST("/post/getPostById")
    Call<Post> getPostById(@Body HashMap<String,String> map);

    @POST("/auth/logout")
    Call<Void> logout(@Header("authorization") String token);

    @POST("user/addPostIdToLikedList")
    Call<Void> addPostIdToLikedList(@Body HashMap<String,String> map);

    @POST("user/getLikedListByUserName")
    Call<List<String>> getLikedListByUserName(@Body HashMap<String,String> map);

    @POST("user/removePostIDFromLikedList")
    Call<Void> removePostIDFromLikedList(@Body HashMap<String,String> map);

    @POST("/location/getLikedLocations")
    Call<List<Location>> getLikedLocations(@Body HashMap<String,String> map);

    @POST("/location/getMyPostsLocations")
    Call<List<Location>> getMyPostsLocations(@Body HashMap<String,String> map);


    @POST("/post/getLikedPostsbyUserName")
    Call<List<Post>> getLikedPostsbyUserName( @Body HashMap<String,String> map);

    @POST("/follower/follow")
    Call<Void> follow( @Body HashMap<String,String> map, @Header("authorization") String access_token);

    @POST("/follower/checkIfFollow")
    Call<Boolean> checkIfFollow( @Body HashMap<String,String> map);


    @POST("/follower/unfollow")
    Call<Void> unfollow( @Body HashMap<String,String> map, @Header("authorization") String access_token);

    @POST("/user/partialUserSearch")
    Call<List<User>> partialSearch(@Body HashMap<String, String> map);

    @POST("/post/partialPostSearch")
    Call<List<Post>> postPartialSearch(@Body HashMap<String, String> map);

    @POST("/location/locationSearch")
    Call<List<Location>> locationSearch(@Body HashMap<String, String> map);

    @POST("/user/getFollowersByUserName")
    Call<List<User>> getFollowersByUserName(@Body HashMap<String,String> map);

    @POST("/user/getFollowingByUserName")
    Call<List<User>> getFollowingByUserName(@Body HashMap<String,String> map);

    @POST("/post/getAllPostsBySortAlgorithm")
    Call<List<Post>> getAllPostsBySortAlgorithm( @Body HashMap<String,String> map);

}
