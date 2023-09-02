package com.example.oktravelapplictaion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.oktravelapplictaion.model.Model;
import com.example.oktravelapplictaion.model.Post;
import java.util.List;

public class ProfileViewModel extends ViewModel {
    LiveData<List<Post>> postsData_list;

    public ProfileViewModel(){

    }

    public void setPostsData_list(String userName, String likedPosts_or_myPosts) {
        this.postsData_list = Model.instance.getAllProfilePosts(userName, likedPosts_or_myPosts);
    }


    public LiveData<List<Post>> getPostsData_list() {
        return postsData_list;
    }
}