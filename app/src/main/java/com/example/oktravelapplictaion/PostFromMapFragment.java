package com.example.oktravelapplictaion;
import static android.content.Context.MODE_PRIVATE;
import static com.example.oktravelapplictaion.ProfileFragment.toBitmap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.oktravelapplictaion.feed.MainActivity;
import com.example.oktravelapplictaion.feed.feedFragmentDirections;
import com.example.oktravelapplictaion.login.LoginActivity;
import com.example.oktravelapplictaion.model.Model;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.HashMap;


public class PostFromMapFragment extends Fragment {

    String postId;
    TextView postTile;
    TextView profileName;
    TextView description;
    ShapeableImageView image_profile;
    VideoView videoView;

    ////
    SharedPreferences sp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_from_map, container, false);
        postTile = view.findViewById(R.id.post_from_map_title_tv);
        profileName = view.findViewById(R.id.post_from_map_profileName_tv);
        description = view.findViewById(R.id.post_from_map_description_tv);
        image_profile = view.findViewById(R.id.circleImageProfile_postFromMapFragment);
        videoView = view.findViewById(R.id.post_from_map_videoView);
        postId = PostFromMapFragmentArgs.fromBundle(getArguments()).getPostId();
        Log.d("POST_ID", "" + postId);


        Model.instance.getPostById(postId, post -> {
            post.getPostID();
            image_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(v).navigate((NavDirections) PostFromMapFragmentDirections.actionPostFromMapFragmentToAnotherUserProfileFragment(post.getUserName()));
                }
            });
        });

        Model.instance.getPostById(postId, post -> {
            postTile.setText(post.getPostTitle());
            description.setText(post.getPostDescription());
            videoView.setVideoPath(post.getVideoUrl());
            videoView.start();

            HashMap<String, String> map = new HashMap<>();
            map.put("userName", post.getUserName());
            Model.instance.checkIfUserExist(map, (user) -> {
                profileName.setText(user.getUserName());
                new ProfileFragment.getImageFromUrl(image_profile).execute(user.getImageUrl());
            });


        });

        return view;
    }


}