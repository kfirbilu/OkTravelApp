package com.example.oktravelapplictaion.feed;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.oktravelapplictaion.ProfileFragment;
import com.example.oktravelapplictaion.R;
import com.example.oktravelapplictaion.model.Model;
import com.example.oktravelapplictaion.model.Post;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.HashMap;

public class feedFragment extends Fragment {
    ViewPager2 postsViewPager;

    FeedFragmentViewModel viewModel;
    MyAdapter adapter;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefresh;
    ImageButton location_feed_btn;
    float longitude;
    float latitude;
    SharedPreferences sp;
    String access_token;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel=new ViewModelProvider(this).get(FeedFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.fragment_feed, container, false);
        sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        access_token= "Bearer "+sp.getString("accessToken","");
        viewModel.setUserName(sp.getString("userName",""));
        swipeRefresh=view.findViewById(R.id.swiperefresh_feed_fragment);
        swipeRefresh.setOnRefreshListener(()->Model.instance.refreshPostsList(sp.getString("userName","")));



        postsViewPager = view.findViewById(R.id.postViewPager);
        adapter = new MyAdapter();
        postsViewPager.setAdapter(adapter);

        setHasOptionsMenu(true);
        viewModel.getPostsData_list().observe(getViewLifecycleOwner(), list -> refresh());
        swipeRefresh.setRefreshing(Model.instance.getPostsListLoadingState().getValue()== Model.PostsListLoadingState.loading);
        Model.instance.getPostsListLoadingState().observe(getViewLifecycleOwner(), new Observer<Model.PostsListLoadingState>() {
            @Override
            public void onChanged(Model.PostsListLoadingState postsListLoadingState) {
                if(postsListLoadingState== Model.PostsListLoadingState.loading){
                    swipeRefresh.setRefreshing(true);
                }else{
                    swipeRefresh.setRefreshing(false);
                }
            }
        });
        return view;
    }

    private void refresh() {
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title_tv;
        VideoView videoView;
        TextView username_tv;
        TextView description_tv;
        TextView numberOfLikes_tv;
        ImageButton like_btn;
        ImageButton liked_btn;
        ImageButton post_details;
        ImageButton share_post;
        ShapeableImageView image_profile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title_tv = itemView.findViewById(R.id.post_item_title_tv);
            videoView = itemView.findViewById(R.id.test_to_like_btn);
            username_tv = itemView.findViewById(R.id.post_item_profileName_tv);
            description_tv = itemView.findViewById(R.id.post_item_description_tv);
            progressBar = itemView.findViewById(R.id.post_item_progressBar);
            image_profile = itemView.findViewById(R.id.circleImageProfile_postItem);
            location_feed_btn = itemView.findViewById(R.id.post_item_location_btn);
            location_feed_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.isFromFeed = true;
                    longitude = viewModel.getPostsData_list().getValue().get(postsViewPager.getCurrentItem()).getLongitude();
                    latitude = viewModel.getPostsData_list().getValue().get(postsViewPager.getCurrentItem()).getLatitude();
                    Navigation.findNavController(v).navigate(feedFragmentDirections.actionFeedFragmentToMapFragment().setLatitude(latitude).setLongitude(longitude));
                }
            });
            post_details = itemView.findViewById(R.id.post_details_post_item_ibtn);


            share_post = itemView.findViewById(R.id.share_post_post_item_ibtn);

            numberOfLikes_tv = itemView.findViewById(R.id.post_item_numberOfLikes_tv);
            like_btn = itemView.findViewById(R.id.post_item_like_ibtn);
            liked_btn = itemView.findViewById(R.id.post_item_liked_ibtn);
            liked_btn.setVisibility(View.GONE);
            like_btn.setVisibility(View.GONE);
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.post_item,parent,false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Post post = viewModel.getPostsData_list().getValue().get(position);
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
                    if(viewModel.getPostsData_list().getValue().size()<=holder.getAdapterPosition()+1) {
                        adapter.bindViewHolder(holder, 0);
                    }
                    else {
                        adapter.bindViewHolder(holder, holder.getAdapterPosition() + 1);
                    }
                }
            });
            holder.numberOfLikes_tv.setText(String.valueOf(post.getLikes()));

            sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
            HashMap<String,String> map = new HashMap<>();
            map.put("userName", post.getUserName());
            Model.instance.checkIfUserExist(map, user -> {
                if (user != null) {
                    if (user.getImageUrl() != null) {
                        new ProfileFragment.getImageFromUrl(holder.image_profile).execute(user.getImageUrl());
                    }

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





            holder.post_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(v).navigate((NavDirections) feedFragmentDirections.actionFeedFragmentToPostDetailsFragment(post.getPostID()));
                }
            });

            holder.title_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(v).navigate((NavDirections) feedFragmentDirections.actionFeedFragmentToPostDetailsFragment(post.getPostID()));
                }
            });

        }






        @Override
        public int getItemCount() {
            if(viewModel.getPostsData_list().getValue() == null){
                return 0;
            }
            return viewModel.getPostsData_list().getValue().size();
        }

        private void add_a_like(Post post, MyViewHolder holder){
            post.setLikes(post.getLikes() + 1);
            Model.instance.editPost(post, access_token, () ->{
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