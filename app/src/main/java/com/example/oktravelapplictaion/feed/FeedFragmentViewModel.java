package com.example.oktravelapplictaion.feed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.oktravelapplictaion.model.Model;
import com.example.oktravelapplictaion.model.Post;

import java.util.List;

public class FeedFragmentViewModel extends ViewModel {
    LiveData<List<Post>> postsData_list;

    String userName;

    public FeedFragmentViewModel(){
        postsData_list= Model.instance.getAll(userName);
    }
    public LiveData<List<Post>> getPostsData_list() {
        return postsData_list;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
