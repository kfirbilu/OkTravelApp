package com.example.oktravelapplictaion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.oktravelapplictaion.feed.feedFragmentDirections;
import com.example.oktravelapplictaion.model.Model;
import com.example.oktravelapplictaion.model.Post;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.HashMap;
import java.util.List;


public class SearchLocationsFragment extends Fragment {

    List<Post> data;
    ViewPager2 postsViewPager;
    MyAdapter myAdapter;
    ProgressBar progressBar;
    ImageButton location_feed_btn;
    float longitude;
    float latitude;
    SharedPreferences sp;
    EditText location_name_et;
    ImageButton search_location_btn;
    String access_token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_locations, container, false);
        sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        access_token= "Bearer "+sp.getString("accessToken","");
        location_name_et=view.findViewById(R.id.location_et_locations_fragment);
        search_location_btn = view.findViewById(R.id.search_btn_locations_fragmnet);
        postsViewPager=view.findViewById(R.id.viewpager_locations_fragment);
        myAdapter = new MyAdapter();
        postsViewPager.setAdapter(myAdapter);
        setHasOptionsMenu(true);
        search_location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> map=new HashMap<>();
                map.put("postTitle", location_name_et.getText().toString());
                Model.instance.postPartialSearch(map,posts -> {
                   data=posts;
                   myAdapter.notifyDataSetChanged();

                });
            }
        });
        return view;
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title_tv;
        VideoView videoView;
        TextView username_tv;
        TextView description_tv;
        TextView numberOfLikes_tv;
        ImageButton like_btn;
        ImageButton liked_btn;
        ShapeableImageView image_profile;
        ImageButton post_details;
        ImageButton share_post;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            share_post = itemView.findViewById(R.id.share_post_post_item_ibtn);
            title_tv = itemView.findViewById(R.id.post_item_title_tv);
            videoView = itemView.findViewById(R.id.test_to_like_btn);
            username_tv = itemView.findViewById(R.id.post_item_profileName_tv);
            description_tv = itemView.findViewById(R.id.post_item_description_tv);
            progressBar = itemView.findViewById(R.id.post_item_progressBar);
            image_profile = itemView.findViewById(R.id.circleImageProfile_postItem);
            post_details = itemView.findViewById(R.id.post_details_post_item_ibtn);
            location_feed_btn = itemView.findViewById(R.id.post_item_location_btn);
            location_feed_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.isFromFeed = true;
                    longitude = data.get(postsViewPager.getCurrentItem()).getLongitude();
                    latitude = data.get(postsViewPager.getCurrentItem()).getLatitude();
                    Navigation.findNavController(v).navigate(TabsSearchFragmentDirections.actionTabsSearchFragmentToMapFragment().setLatitude(latitude).setLongitude(longitude));
                }
            });

            numberOfLikes_tv = itemView.findViewById(R.id.post_item_numberOfLikes_tv);
            like_btn = itemView.findViewById(R.id.post_item_like_ibtn);
            liked_btn = itemView.findViewById(R.id.post_item_liked_ibtn);
            liked_btn.setVisibility(View.GONE);
            like_btn.setVisibility(View.GONE);
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.post_item,parent,false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Post post = data.get(position);
            holder.title_tv.setText(post.getPostTitle());
            holder.videoView.setVideoPath(post.getVideoUrl());
            holder.description_tv.setText(post.getPostDescription());
            holder.username_tv.setText(post.getUserName());
            holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    progressBar.setVisibility(View.GONE);
                    mp.start();
                    float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
                    float screenRatio = holder.videoView.getWidth() / (float) holder.videoView.getHeight();
                    float scale = videoRatio / screenRatio;

                    if (scale >= 1f) {
                        holder.videoView.setScaleX(scale);
                    } else {
                        holder.videoView.setScaleY(1f / scale);
                    }
                }
            });
            holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start();
                }
            });
            holder.numberOfLikes_tv.setText(String.valueOf(post.getLikes()));

            sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
            HashMap<String,String> map = new HashMap<>();
            map.put("userName",sp.getString("userName",""));
            Model.instance.checkIfUserExist(map, user -> {
                if(user.getImageUrl() != null){
                    new ProfileFragment.getImageFromUrl(holder.image_profile).execute(user.getImageUrl());
                }

            });

            Model.instance.getLikedListByUserName(sp.getString("userName",""), likedList -> {
                if (likedList == null){
                    holder.like_btn.setVisibility(View.VISIBLE);
                    holder.liked_btn.setVisibility(View.GONE);
                }
                else {
                    String current_postID = post.getPostID();
                    Boolean liked_post = false;
                    for (int i = 0; i < likedList.size(); i++) {
                        if (current_postID.equals(likedList.get(i))) {
                            liked_post = true;
                            break;
                        }
                    }
                    if (liked_post) {
                        holder.like_btn.setVisibility(View.GONE);
                        holder.liked_btn.setVisibility(View.VISIBLE);
                    } else {
                        holder.like_btn.setVisibility(View.VISIBLE);
                        holder.liked_btn.setVisibility(View.GONE);
                    }
                }
            });

            holder.image_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(v).navigate((NavDirections) feedFragmentDirections.actionFeedFragmentToAnotherUserProfileFragment(post.getUserName()));

                }
            });

            holder.like_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    add_a_like(post, holder);
                    add_postId_to_likedList(post, holder);
                    holder.liked_btn.setVisibility(View.VISIBLE);
                    holder.like_btn.setVisibility(View.GONE);
                }
            });

            holder.liked_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove_a_like(post, holder);
                    remove_postId_from_likedList(post, holder);
                    holder.like_btn.setVisibility(View.VISIBLE);
                    holder.liked_btn.setVisibility(View.GONE);
                }
            });

            holder.share_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT,"Look at this post on OkTravel!\n\n" +
                            "http://oktravel.co.il/posts/"+post.getPostID());
                    shareIntent.setType("text/plain");
                    startActivity(shareIntent);
                }
            });

            holder.title_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(v).navigate(TabsSearchFragmentDirections.actionTabsSearchFragmentToPostDetailsFragment2(
                            post.getPostID()
                    ));
                }
            });
        }

        @Override
        public int getItemCount() {
            if(data == null){
                return 0;
            }
            return data.size();
        }

        private void add_a_like(Post post, MyViewHolder holder){
            post.setLikes(post.getLikes() + 1);
            Model.instance.editPost(post,access_token, () ->{
                holder.numberOfLikes_tv.setText(String.valueOf(post.getLikes()));
            });
        }

        private void remove_a_like(Post post, MyViewHolder holder){
            post.setLikes(post.getLikes() - 1);
            Model.instance.editPost(post, access_token, () ->{
                holder.numberOfLikes_tv.setText(String.valueOf(post.getLikes()));
            });
        }

        private void add_postId_to_likedList(Post post, MyViewHolder holder){
            sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
            HashMap<String,String> map = new HashMap<>();
            map.put("userName", sp.getString("userName",""));
            map.put("postID", post.getPostID());
            Model.instance.addPostIdToLikedList(map, () -> {});
        }

        private void remove_postId_from_likedList(Post post, MyViewHolder holder){
            sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
            HashMap<String,String> map = new HashMap<>();
            map.put("userName", sp.getString("userName",""));
            map.put("postID", post.getPostID());
            Model.instance.removePostIDFromLikedList(map, () -> {});
        }
    }
}