package com.example.oktravelapplictaion.model;

import android.content.Context;
import android.net.Uri;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.oktravelapplictaion.MyApplication;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Model {
    public static final Model instance = new Model();
    // map vars
    public static ArrayList<MarkerOptions> locationsList = new ArrayList<>();
    public static Boolean isFromNewPost = false;
    public static Boolean isFromEditPost = false;
    public static Boolean isFromFeed = false;
    public Executor executor = Executors.newFixedThreadPool(1);
    public Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());
    ModelMongo modelMongo = new ModelMongo();


    public static String COLMAN_SERVER_URL = "http://193.106.55.132:3000/";
    public static String LOCALHOST_URL = "http://127.0.0.1:3000/";


    public enum PostsListLoadingState {
        loading,
        loaded
    }

    MutableLiveData<PostsListLoadingState> postsListLoadingState = new MutableLiveData<PostsListLoadingState>();
    MutableLiveData<List<Post>> postsList = new MutableLiveData<List<Post>>();
    MutableLiveData<PostsListLoadingState> postsProfileListLoadingState = new MutableLiveData<PostsListLoadingState>();
    MutableLiveData<List<Post>> postsProfileList = new MutableLiveData<List<Post>>();

    private Model() {
        postsListLoadingState.setValue(PostsListLoadingState.loaded);
        postsProfileListLoadingState.setValue(PostsListLoadingState.loaded);
    }

    public LiveData<PostsListLoadingState> getPostsProfileListLoadingState() {
        return postsProfileListLoadingState;
    }

    public LiveData<List<Post>> getAllProfilePosts(String userName, String likedPosts_or_myPosts) {
        if (postsProfileList.getValue() == null) {
            refreshProfilePostsList(userName, likedPosts_or_myPosts);
        }
        return postsProfileList;
    }

    public void refreshProfilePostsList(String userName, String likedPosts_or_myPosts) {
        postsProfileListLoadingState.setValue(PostsListLoadingState.loading);

        if (likedPosts_or_myPosts == "myPosts") {
            modelMongo.getPostsbyUserName(userName, new ModelMongo.GetAllPostsbyUserName() {
                @Override
                public void onComplete(List<Post> list) {
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            List<Post> postList = new LinkedList<>();
                            if (list != null) {
                                for (Post post : list) {
                                    if (!post.isDeleted) {
                                        postList.add(post);
                                    }
                                }
                                postsProfileList.postValue(postList);
                            }
                            postsProfileListLoadingState.postValue(PostsListLoadingState.loaded);
                        }
                    });
                }
            });
        } else { // from likedPosts
            modelMongo.getLikedPostsbyUserName(userName, new ModelMongo.GetAllLikedPostsbyUserName() {
                @Override
                public void onComplete(List<Post> list) {
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            List<Post> postList = new LinkedList<>();
                            if (list != null) {
                                for (Post post : list) {
                                    if (!post.isDeleted) {
                                        postList.add(post);
                                    }
                                }
                                postsProfileList.postValue(postList);
                            }
                            postsProfileListLoadingState.postValue(PostsListLoadingState.loaded);
                        }
                    });
                }
            });
        }

    }

    public LiveData<PostsListLoadingState> getPostsListLoadingState() {
        return postsListLoadingState;
    }

    public LiveData<List<Post>> getAll(String userName) {
        if (postsList.getValue() == null) {
            refreshPostsList(userName);
        }
        return postsList;
    }

    public void refreshPostsList(String userName) {
        postsListLoadingState.setValue(PostsListLoadingState.loading);
        Long lastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("PostsLastUpdateDate", 0);

        modelMongo.getAllPosts(lastUpdateDate, userName, new ModelMongo.GetAllPostsListener() {
            @Override
            public void onComplete(List<Post> list) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Long lud = new Long(0);
                        if (list != null) {
                            Log.d("TAG", "returned" + list.size());
                            for (Post post : list) {
                                if (post.isDeleted == false) {
                                    AppLocalDb.db.postDao().insertAll(post);
                                    if (lud < post.getUpdateDate()) {
                                        lud = post.getUpdateDate();
                                    }
                                } else {
                                    AppLocalDb.db.postDao().delete(post);
                                }
                            }
                            MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).edit().
                                    putLong("PostsLastUpdateDate", lud).
                                    commit();
                            List<Post> postList = AppLocalDb.db.postDao().getAll();
                            postsList.postValue(postList);
                        }
                        postsListLoadingState.postValue(PostsListLoadingState.loaded);

                    }
                });
            }
        });

    }


    public interface AddUserListener {
        void onComplete();
    }

    public void addUser(User user, AddUserListener listener) {
        modelMongo.addUser(user, listener);
    }


    public interface AddPostListener {
        void onComplete();
    }

    public void addPost(Post post, String access_token, AddPostListener listener) {
        modelMongo.addPost(post, access_token, new AddPostListener() {
            @Override
            public void onComplete() {
                listener.onComplete();
            }
        });
    }

    public interface FollowListener {
        void onComplete();
    }

    public void follow(HashMap<String, String> map, String access_token, FollowListener listener) {
        modelMongo.follow(map, access_token, new FollowListener() {
            @Override
            public void onComplete() {
                listener.onComplete();
            }
        });
    }

    public interface unFollowListener {
        void onComplete();
    }

    public void unfollow(HashMap<String, String> map, String access_token, unFollowListener listener) {
        modelMongo.unfollow(map, access_token, new unFollowListener() {
            @Override
            public void onComplete() {
                listener.onComplete();
            }
        });
    }

    public interface checkIfFollowListener {
        void onComplete(boolean found);
    }

    public void checkIfFollow(HashMap<String, String> map, checkIfFollowListener listener) {
        modelMongo.checkIfFollow(map, (found) -> {
            listener.onComplete(found);
        });
    }

    public interface LogoutListener {
        void onComplete();
    }

    public void logOut(String token, LogoutListener listener) {
        modelMongo.logOut(token, new LogoutListener() {
            @Override
            public void onComplete() {
                listener.onComplete();
            }
        });
    }

    public interface LoginListener {
        void onComplete(HashMap<String, String> map);
    }

    public String checkLogin(HashMap map, LoginListener listener) {
        modelMongo.checkLogin(map, listener);
        return null;
    }

    public interface LoginAppsListener {
        void onComplete(HashMap<String, String> map);
    }

    public String loginApps(HashMap map, LoginAppsListener listener) {
        modelMongo.loginApps(map, listener);
        return null;
    }

    public interface GetUserByUserNameByListener {
        void onComplete(User user);
    }

    public User checkIfUserExist(HashMap map, GetUserByUserNameByListener listener) {
        modelMongo.getUserByUserName(map, listener);
        return null;
    }

    public interface partialSearchListener {
        void onComplete(List<User> users);
    }

    public List<User> partialSearch(HashMap<String, String> map, partialSearchListener listener) {
        modelMongo.partialSearch(map, listener);
        return null;
    }


    public interface GetFollowersByUserNameListener {
        void onComplete(List<User> users);
    }

    public List<User> getFollowersByUserName(String userName, GetFollowersByUserNameListener listener) {
        modelMongo.getFollowersByUserName(userName, listener);
        return null;
    }


    public interface postPartialSearchListener {
        void onComplete(List<Post> posts);
    }

    public List<User> postPartialSearch(HashMap<String, String> map, postPartialSearchListener listener) {
        modelMongo.postPartialSearch(map, listener);
        return null;
    }

    public interface GetUserByEmailListener {
        void onComplete(User user);
    }


    public interface locationSearchListener {
        void onComplete(List<Location> locations);
    }

    public List<Location> locationSearch(HashMap<String, String> map, locationSearchListener listener) {
        modelMongo.locationSearch(map, listener);
        return null;
    }


    //Map

    public interface GetAllLocationsListener {
        void onComplete(List<Location> userList);
    }

    public void getAllLocations(GetAllLocationsListener listener) {
        modelMongo.getAllLocations(listener);
    }

    public interface GetPostByIdListener {
        void onComplete(Post post);
    }

    public Post getPostById(String post_id, GetPostByIdListener listener) {
        modelMongo.getPostById(post_id, listener);
        return null;
    }

    public interface EditUserListener {
        void onComplete();
    }

    public void editUser(HashMap<String, String> map, EditUserListener listener) {
        modelMongo.editUser(map, listener);
    }

    public interface EditPostListener {
        void onComplete();
    }

    public void editPost(Post post, String access_token, EditPostListener listener) {
        modelMongo.editPost(post, access_token, listener);
    }

    public interface DeletePostListener {
        void onComplete();
    }

    public void deletePost(String postid, String access_token, EditPostListener listener) {
        modelMongo.deletePost(postid, access_token, listener);
    }


    public interface AddPostIdToLikedListListener {
        void onComplete();
    }

    public void addPostIdToLikedList(HashMap<String, String> map, AddPostIdToLikedListListener listener) {
        modelMongo.addPostIdToLikedList(map, listener);
    }

    public interface GetLikedListListener {
        void onComplete(List<String> likedList);
    }

    public void getLikedListByUserName(String userName, GetLikedListListener listener) {
        modelMongo.getLikedListByUserName(userName, listener);
    }

    public interface RemovePostIDFromLikedListListener {
        void onComplete();
    }

    public void removePostIDFromLikedList(HashMap<String, String> map, RemovePostIDFromLikedListListener listener) {
        modelMongo.removePostIDFromLikedList(map, listener);
    }

    public interface UploadVideoListener {
        void onComplete(String video_url);
    }

    public String uploadVideo(File file, UploadVideoListener listener) {
        Retrofit retrofit = ModelMongo.getRetrofitForUpload();
        RequestBody requestBody = RequestBody.create(MediaType.parse("video/*"), file);
        MultipartBody.Part parts = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody someData = RequestBody.create(MediaType.parse("text/plain"), "this is a video");
        UploadApis uploadApis = retrofit.create(UploadApis.class);
        Call call = uploadApis.uploadVideo(parts, someData);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Response r = response;
                String videoPathInServer = r.body().toString();
                listener.onComplete(videoPathInServer);
                Log.d("RES - OK", "" + response.body());
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d("RES - FAILE", "" + t.toString());
            }
        });
        return null;
    }


    public interface GetLikedLocationsListener {
        void onComplete(List<Location> locationsList);
    }

    public void getLikedLocations(String userName, GetLikedLocationsListener listener) {
        modelMongo.getLikedLocations(userName, listener);
    }

    public interface GetMyPostsLocationsListener {
        void onComplete(List<Location> locationsList);
    }

    public void getMyPostsLocations(String userName, GetMyPostsLocationsListener listener) {
        modelMongo.getMyPostsLocations(userName, listener);
    }


    public interface GetFollowingByUserNameListener {
        void onComplete(List<User> followingList);
    }

    public List<User> getFollowingByUserName(String userName, GetFollowingByUserNameListener listener) {
        modelMongo.getFollowingByUserName(userName, listener);
        return null;
    }


    //////////// upload image

    public interface UploadImageListener {
        void onComplete(String Image_url);
    }


    public String uploadImage(File file, UploadImageListener listener) {
        Retrofit retrofit = ModelMongo.getRetrofitForUpload();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part parts = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody someData = RequestBody.create(MediaType.parse("text/plain"), "this is an image");
        UploadApis uploadApis = retrofit.create(UploadApis.class);
        Call call = uploadApis.uploadVideo(parts, someData);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Response r = response;
                String imagePathInServer = r.body().toString();
                listener.onComplete(imagePathInServer);
                Log.d("TAG", "YAYYYY");
                Log.d("RES - OK", "" + response.body());
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d("TAG", "nooooooooo");
                Log.d("RES - FAILE", "" + t.toString());
            }
        });
        return null;
    }


    public void refreshPostsListSorted(String userName) {
        postsListLoadingState.setValue(PostsListLoadingState.loading);
        Long lastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("PostsLastUpdateDate", 0);


        modelMongo.getAllPostsBySortAlgorithm(lastUpdateDate, userName, new ModelMongo.GetAllPostsBySortAlgorithmListener() {
            @Override
            public void onComplete(List<Post> list) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Long lud = new Long(0);
                        if (list != null) {
                            Log.d("TAG", "returned" + list.size());
                            for (Post post : list) {
                                if (post.isDeleted == false) {
                                    AppLocalDb.db.postDao().insertAll(post);
                                    if (lud < post.getUpdateDate()) {
                                        lud = post.getUpdateDate();
                                    }
                                } else {
                                    AppLocalDb.db.postDao().delete(post);
                                }
                            }
                            MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).edit().
                                    putLong("PostsLastUpdateDate", lud).
                                    commit();
                            List<Post> postList = AppLocalDb.db.postDao().getAll();
                            postsList.postValue(postList);
                        }
                        postsListLoadingState.postValue(PostsListLoadingState.loaded);

                    }
                });
            }
        });

    }


}