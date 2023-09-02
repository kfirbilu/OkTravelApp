package com.example.oktravelapplictaion.model;

import com.example.oktravelapplictaion.login.LoginResult;
import com.example.oktravelapplictaion.login.RetrofitInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ModelMongo {

//    private static String BASE_URL="http://10.0.2.2:3000";   // LOCAL URL
        private static String BASE_URL="http://193.106.55.132:3000";  // COLMAN URL
    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);




    /////////////////

    private static Retrofit retrofitForUpload;
    public static String COLMAN_SERVER_URL= "http://193.106.55.132:3000/";
//public static String COLMAN_SERVER_URL= "http://10.0.2.2:3000";
    public static String LOCALHOST_URL= "http://127.0.0.1:3000/";


    public static Retrofit getRetrofitForUpload(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        if(retrofitForUpload==null) {
            retrofitForUpload = new Retrofit.Builder().baseUrl(COLMAN_SERVER_URL).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        }
        return retrofitForUpload;
    }



    public void addUser(User user, Model.AddUserListener listener) {
        HashMap<String,Object> map = user.toJson();
        map.put("likedList", "a");
        Call<Void> call = retrofitInterface.executeSignup(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    listener.onComplete();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onComplete();
            }
        });
    }

    public void getUserByUserName(HashMap map, Model.GetUserByUserNameByListener listener) {
        Call<LoginResult> call = retrofitInterface.findByUserName(map);
        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if(response.code() == 200){
                    LoginResult result = response.body();
                    User user = new User(result.getUserName()," ",result.getEmail(),result.getPhoneNumber(),result.getImageUrl());
                    listener.onComplete(user);
                }
                else{
                    listener.onComplete(null);
                }
            }
            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                listener.onComplete(null);
            }
        });
    }

    public void checkLogin(HashMap map, Model.LoginListener listener) {
        Call<LoginResult> call = retrofitInterface.executeLogin(map);
        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                HashMap<String,String> map = new HashMap<>();
                if(response.code() == 200){
                    LoginResult result = response.body();
                    map.put("accessToken", result.getAccessToken());
                    map.put("refreshToken", result.getRefreshToken());
                    listener.onComplete(map);
                }
                else {
                    listener.onComplete(null);
                }
            }
            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                listener.onComplete(null);
            }
        });
    }
    public void loginApps(HashMap map, Model.LoginAppsListener listener) {
        Call<LoginResult> call = retrofitInterface.loginApps(map);
        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                HashMap<String,String> map = new HashMap<>();
                if(response.code() == 200){
                    LoginResult result = response.body();
                    map.put("accessToken", result.getAccessToken());
                    map.put("refreshToken", result.getRefreshToken());
                    listener.onComplete(map);
                }
                else {
                    listener.onComplete(null);
                }
            }
            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                listener.onComplete(null);
            }
        });
    }

    public void addPost(Post post, String access_token, Model.AddPostListener listener) {
        HashMap<String,Object> map = post.toJson();
        Call<Void> call = retrofitInterface.addNewPost(map, access_token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    listener.onComplete();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onComplete();
            }
        });
    }

    public void logOut(String token, Model.LogoutListener listener) {
        Call<Void> call = retrofitInterface.logout(token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    listener.onComplete();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onComplete();
            }
        });
    }

    public void getAllLocations(Model.GetAllLocationsListener listener) {
        Call<List<Location>> call = retrofitInterface.getAllLocations();
        call.enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                if(response.code() == 200){
                    List<Location> locations = new ArrayList<>();
                    locations=response.body();
                    listener.onComplete(locations);
                }
                else{
                    listener.onComplete(null);
                }
            }
            @Override
            public void onFailure(Call<List<Location>> call, Throwable t) {
                listener.onComplete(null);
            }
        });
    }

    public void editUser(HashMap<String, String> map, Model.EditUserListener listener) {
        Call<Void> call = retrofitInterface.editUser(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    listener.onComplete();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onComplete();
            }
        });
    }

    public void getPostById(String post_id, Model.GetPostByIdListener listener) {
        HashMap<String,String> map = new HashMap<>();
        map.put("post_id", post_id);
        Call<Post> call = retrofitInterface.getPostById(map);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(response.code() == 200){
                    Post result = response.body();
//                    User user = new User(result.getUserName()," ",result.getEmail(),result.getPhoneNumber(),result.getImageUrl());
                    listener.onComplete(result);
                }
                else{
                    listener.onComplete(null);
                }
            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                listener.onComplete(null);
            }
        });
    }

    public void editPost(Post post, String access_token, Model.EditPostListener listener) {
        Call<Void> call = retrofitInterface.editPost(post.toJson(), access_token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    listener.onComplete();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onComplete();
            }
        });
    }

    public void deletePost(String postid, String access_token, Model.EditPostListener listener) {
        HashMap<String,String> map = new HashMap<>();
        map.put("postid", postid);
        Call<Void> call = retrofitInterface.deletePost(map, access_token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    listener.onComplete();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onComplete();
            }
        });
    }

    public void follow(HashMap<String, String> map, String access_token, Model.FollowListener followListener) {
        Call<Void> call = retrofitInterface.follow(map,access_token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    followListener.onComplete();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                followListener.onComplete();
            }
        });
    }

    public void checkIfFollow(HashMap<String,String> map, Model.checkIfFollowListener listener) {
        Call<Boolean> call = retrofitInterface.checkIfFollow(map);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.code() == 200){
                    Boolean found = response.body();
                    listener.onComplete(found);
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                listener.onComplete(false);
            }
        });

    }

    public void unfollow(HashMap<String, String> map, String access_token, Model.unFollowListener listener) {
        Call<Void> call = retrofitInterface.unfollow(map, access_token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    listener.onComplete();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onComplete();
            }
        });
    }

    public void partialSearch(HashMap<String, String> map, Model.partialSearchListener listener) {
        Call<List<User>> call = retrofitInterface.partialSearch(map);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.code() == 200){
                    List<User> users = new ArrayList<>();
                    users=response.body();
                    listener.onComplete(users);
                }
                else{
                    listener.onComplete(null);
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                listener.onComplete(null);
            }
        });
    }

    public void postPartialSearch(HashMap<String, String> map, Model.postPartialSearchListener listener) {
        Call<List<Post>> call = retrofitInterface.postPartialSearch(map);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(response.code() == 200){
                    List<Post> posts = new ArrayList<>();
                    posts=response.body();
                    listener.onComplete(posts);
                }
                else{
                    listener.onComplete(null);
                }
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                listener.onComplete(null);
            }
        });
    }


    public interface GetAllPostsListener{
        void onComplete(List<Post> list);
    }
    public void getAllPosts(Long lastUpdateDate, String userName, GetAllPostsListener listener) {
        HashMap<String,String> map = new HashMap<>();
        map.put("updateDate", Long.toString(lastUpdateDate));
        map.put("userName", userName);
        Call<List<Post>> call = retrofitInterface.getAllPosts(map);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(response.code() == 200){
                    List<Post> posts = new ArrayList<>();
                    posts=response.body();
                    listener.onComplete(posts);
                }
                else{
                    listener.onComplete(null);
                }
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                listener.onComplete(null);
            }
        });
    }

    public interface GetAllPostsbyUserName{
        void onComplete(List<Post> list);
    }

    public void getPostsbyUserName(String userName, GetAllPostsbyUserName listener) {
        HashMap<String,String> map = new HashMap<>();
        map.put("userName", userName);
        Call<List<Post>> call = retrofitInterface.getPostsbyUsername(map);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(response.code() == 200){
                    List<Post> posts = new ArrayList<>();
                    posts=response.body();
                    listener.onComplete(posts);
                }
                else{
                    listener.onComplete(null);
                }
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                listener.onComplete(null);
            }
        });
    }


    public void addPostIdToLikedList(HashMap<String, String> map, Model.AddPostIdToLikedListListener listener) {
        Call<Void> call = retrofitInterface.addPostIdToLikedList(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    listener.onComplete();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onComplete();
            }
        });
    }


    public void getLikedListByUserName(String userName, Model.GetLikedListListener listener) {
        HashMap<String,String> map = new HashMap<>();
        map.put("userName", userName);
        Call<List<String>> call = retrofitInterface.getLikedListByUserName(map);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if(response.code() == 200){
                    List<String> likedList = new ArrayList<>();
                    likedList=response.body();
                    listener.onComplete(likedList);
                }
                else{
                    listener.onComplete(null);
                }
            }
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                listener.onComplete(null);
            }
        });
    }





    public void removePostIDFromLikedList(HashMap<String, String> map, Model.RemovePostIDFromLikedListListener listener) {
        Call<Void> call = retrofitInterface.removePostIDFromLikedList(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    listener.onComplete();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onComplete();
            }
        });
    }

    public void getLikedLocations(String userName, Model.GetLikedLocationsListener listener) {
        HashMap<String,String> map = new HashMap<>();
        map.put("userName", userName);
        Call<List<Location>> call = retrofitInterface.getLikedLocations(map);
        call.enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                if(response.code() == 200){
                    List<Location> locations = new ArrayList<>();
                    locations=response.body();
                    listener.onComplete(locations);
                }
                else{
                    listener.onComplete(null);
                }
            }
            @Override
            public void onFailure(Call<List<Location>> call, Throwable t) {
                listener.onComplete(null);
            }
        });
    }

    public void getMyPostsLocations(String userName, Model.GetMyPostsLocationsListener listener) {
        HashMap<String,String> map = new HashMap<>();
        map.put("userName", userName);
        Call<List<Location>> call = retrofitInterface.getMyPostsLocations(map);
        call.enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                if(response.code() == 200){
                    List<Location> locations = new ArrayList<>();
                    locations=response.body();
                    listener.onComplete(locations);
                }
                else{
                    listener.onComplete(null);
                }
            }
            @Override
            public void onFailure(Call<List<Location>> call, Throwable t) {
                listener.onComplete(null);
            }
        });
    }

    public interface GetAllLikedPostsbyUserName{
        void onComplete(List<Post> list);
    }

    public void getLikedPostsbyUserName(String userName, GetAllLikedPostsbyUserName listener) {
        HashMap<String,String> map = new HashMap<>();
        map.put("userName", userName);
        Call<List<Post>> call = retrofitInterface.getLikedPostsbyUserName(map);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(response.code() == 200){
                    List<Post> posts = new ArrayList<>();
                    posts=response.body();
                    listener.onComplete(posts);
                }
                else{
                    listener.onComplete(null);
                }
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                listener.onComplete(null);
            }
        });
    }

    public void locationSearch(HashMap<String, String> map, Model.locationSearchListener listener) {
        Call<List<Location>> call = retrofitInterface.locationSearch(map);
        call.enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                if(response.code() == 200){
                    List<Location> locations = new ArrayList<>();
                    locations=response.body();
                    listener.onComplete(locations);
                }
                else{
                    listener.onComplete(null);
                }
            }
            @Override
            public void onFailure(Call<List<Location>> call, Throwable t) {
                listener.onComplete(null);
            }
        });
    }

    public void getFollowersByUserName(String userName, Model.GetFollowersByUserNameListener listener) {
        HashMap<String,String> map = new HashMap<>();
        map.put("userName", userName);
        Call<List<User>> call = retrofitInterface.getFollowersByUserName(map);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.code() == 200){
                    List<User> users = new ArrayList<>();
                    users=response.body();
                    listener.onComplete(users);
                }
                else{
                    listener.onComplete(null);
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                listener.onComplete(null);
            }
        });
    }

    public void getFollowingByUserName(String userName, Model.GetFollowingByUserNameListener listener) {
        HashMap<String,String> map = new HashMap<>();
        map.put("userName", userName);
        Call<List<User>> call = retrofitInterface.getFollowingByUserName(map);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.code() == 200){
                    List<User> users = new ArrayList<>();
                    users=response.body();
                    listener.onComplete(users);
                }
                else{
                    listener.onComplete(null);
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                listener.onComplete(null);
            }
        });
    }


    public interface GetAllPostsBySortAlgorithmListener{
        void onComplete(List<Post> list);
    }

    public void getAllPostsBySortAlgorithm(Long lastUpdateDate, String userName, GetAllPostsBySortAlgorithmListener listener) {
        HashMap<String,String> map = new HashMap<>();
        map.put("updateDate", Long.toString(lastUpdateDate));
        map.put("userName", userName);
        Call<List<Post>> call = retrofitInterface.getAllPostsBySortAlgorithm(map);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(response.code() == 200){
                    List<Post> posts = new ArrayList<>();
                    posts=response.body();
                    listener.onComplete(posts);
                }
                else{
                    listener.onComplete(null);
                }
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                listener.onComplete(null);
            }
        });
    }


}
