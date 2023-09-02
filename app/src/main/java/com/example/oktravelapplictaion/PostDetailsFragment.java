package com.example.oktravelapplictaion;

import static android.content.Context.MODE_PRIVATE;
import static com.example.oktravelapplictaion.ProfileFragment.toBitmap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.oktravelapplictaion.feed.MainActivity;
import com.example.oktravelapplictaion.feed.feedFragment;
import com.example.oktravelapplictaion.feed.feedFragmentDirections;
import com.example.oktravelapplictaion.login.LoginActivity;
import com.example.oktravelapplictaion.model.Model;
import com.example.oktravelapplictaion.model.Post;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostDetailsFragment extends Fragment {

    TextView title_tv;
    VideoView videoView;
    TextView username_tv;
    TextView description_tv;
    TextView numberOfLikes_tv;
    ImageButton share_post;
    ShapeableImageView image_profile;
    ImageButton location_feed_btn;
    float longitude;
    float latitude;
    SharedPreferences sp;
    CheckBox category_1;
    CheckBox category_2;
    CheckBox category_3;
    CheckBox category_4;
    CheckBox category_5;
    CheckBox category_6;
    CheckBox category_7;
    CheckBox category_8;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_details , container, false);




        ////// check if user was logged in - deep links

        sp = getContext().getSharedPreferences("login",MODE_PRIVATE);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if(!sp.getBoolean("logged",true)){
                    toLoginActivity();
                }
            }
        });
        ////// check if user was logged in - deep links



        title_tv = view.findViewById(R.id.post_details_title_tv);
        videoView = view.findViewById(R.id.test_to_like_post_details_btn);
        username_tv = view.findViewById(R.id.post_details_profileName_tv);
        description_tv = view.findViewById(R.id.post_details_description_tv);
        image_profile = view.findViewById(R.id.circleImageProfile_post_details);
        location_feed_btn = view.findViewById(R.id.post_details_location_btn);
        share_post = view.findViewById(R.id.share_post_post_details_ibtn);


        category_1 = view.findViewById(R.id.category_1_postDetails_cb);
        category_2 = view.findViewById(R.id.category_2_postDetails_cb);
        category_3 = view.findViewById(R.id.category_3_postDetails_cb);
        category_4 = view.findViewById(R.id.category_4_postDetails_cb);
        category_5 = view.findViewById(R.id.category_5_postDetails_cb);
        category_6 = view.findViewById(R.id.category_6_postDetails_cb);
        category_7 = view.findViewById(R.id.category_7_postDetails_cb);
        category_8 = view.findViewById(R.id.category_8_postDetails_cb);

        category_1.setVisibility(View.GONE);
        category_2.setVisibility(View.GONE);
        category_3.setVisibility(View.GONE);
        category_4.setVisibility(View.GONE);
        category_5.setVisibility(View.GONE);
        category_6.setVisibility(View.GONE);
        category_7.setVisibility(View.GONE);
        category_8.setVisibility(View.GONE);






        String postID = PostDetailsFragmentArgs.fromBundle(getArguments()).getPostID();
        Model.instance.getPostById(postID, post -> {


            String userName = post.getUserName();
            String postTitle = post.getPostTitle();
            String videoUrl = post.getVideoUrl();
            String postDescription = post.getPostDescription();
            String categoriesList_string = post.getCategoriesList();
            Float postLongitude = post.getLongitude();
            Float postLatitude = post.getLatitude();

            List<String> categoriesList = new ArrayList<>();
            for (int i = 0; i < categoriesList_string.split(",").length; i++){
                categoriesList.add(categoriesList_string.split(",")[i]);
            }

            if(categoriesList.size() >= 1){
                category_1.setVisibility(View.VISIBLE);
                category_1.setChecked(true);
                category_1.setClickable(false);
                category_1.setText(categoriesList.get(0));
            }
            if(categoriesList.size() >= 2){
                category_2.setVisibility(View.VISIBLE);
                category_2.setChecked(true);
                category_2.setClickable(false);
                category_2.setText(categoriesList.get(1));
            }
            if(categoriesList.size() >= 3){
                category_3.setVisibility(View.VISIBLE);
                category_3.setChecked(true);
                category_3.setClickable(false);
                category_3.setText(categoriesList.get(2));
            }
            if(categoriesList.size() >= 4){
                category_4.setVisibility(View.VISIBLE);
                category_4.setChecked(true);
                category_4.setClickable(false);
                category_4.setText(categoriesList.get(3));
            }
            if(categoriesList.size() >= 5){
                category_5.setVisibility(View.VISIBLE);
                category_5.setChecked(true);
                category_5.setClickable(false);
                category_5.setText(categoriesList.get(4));
            }
            if(categoriesList.size() >= 6){
                category_6.setVisibility(View.VISIBLE);
                category_6.setChecked(true);
                category_6.setClickable(false);
                category_6.setText(categoriesList.get(5));
            }
            if(categoriesList.size() >= 7){
                category_7.setVisibility(View.VISIBLE);
                category_7.setChecked(true);
                category_7.setClickable(false);
                category_7.setText(categoriesList.get(6));
            }
            if(categoriesList.size() >= 8){
                category_8.setVisibility(View.VISIBLE);
                category_8.setChecked(true);
                category_8.setClickable(false);
                category_8.setText(categoriesList.get(7));
            }

            location_feed_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.isFromFeed = true;
                    Navigation.findNavController(v).navigate(PostDetailsFragmentDirections.actionPostDetailsFragment2ToMapFragment().setLatitude(postLatitude).setLongitude(postLongitude));
                }
            });

            share_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT,"Look at this post on OkTravel!\n\n" +
                            "http://oktravel.co.il/posts/"+postID);
                    shareIntent.setType("text/plain");
                    startActivity(shareIntent);

                }
            });


            title_tv.setText(postTitle);
            videoView.setVideoPath(videoUrl);
            description_tv.setText(postDescription);
            username_tv.setText(userName);
            HashMap<String,String> map = new HashMap<>();
            map.put("userName", userName);
            Model.instance.checkIfUserExist(map, user -> {
                new ProfileFragment.getImageFromUrl(image_profile).execute(user.getImageUrl());
            });


            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
                    float screenRatio = videoView.getWidth() / (float) videoView.getHeight();
                    float scale = videoRatio / screenRatio;

                    if (scale >= 1f) {
                        videoView.setScaleX(scale);
                    } else {
                        videoView.setScaleY(1f / scale);
                    }
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start();
                }
            });
            HashMap<String,String> map_for_userName = new HashMap<>();
            map_for_userName.put("userName", userName);
            Model.instance.checkIfUserExist(map_for_userName, user -> {
                if(user.getImageUrl() != null){
                    new ProfileFragment.getImageFromUrl(image_profile).execute(user.getImageUrl());
                }
            });

            image_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(v).navigate((NavDirections) PostDetailsFragmentDirections.actionPostDetailsFragment2ToAnotherUserProfileFragment(userName));
                }
            });



        });



        return view;
    }


    ////// check if user was logged in - deep links

    private void toLoginActivity() {
        Intent intent = new Intent(this.getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void toFeedActivity() {
        Intent intent = new Intent(this.getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();

    }
}



