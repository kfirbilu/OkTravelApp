package com.example.oktravelapplictaion;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.oktravelapplictaion.model.Model;
import com.example.oktravelapplictaion.model.Post;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.HashMap;


public class AnotherUserProfileFragment extends Fragment {

    ImageButton logout_btn;
    ImageButton edit_profile_btn;
    ImageButton edit_btn;
    TextView profileName;
    MyAdapter adapter;
    ProfileViewModel viewModel;
    ViewPager2 postsViewPager;
    ProgressBar progressBar;
    String username;
    String post_id;
    SwipeRefreshLayout swipeRefresh;
    SharedPreferences sp;
    ImageButton my_posts;
    ImageButton liked_posts;
    ShapeableImageView image_profile;
    String userName;
    TextView number_of_posts;
    TextView number_of_followers;
    TextView number_of_following;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);

        viewModel.setPostsData_list(userName, "myPosts");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_another_user_profile, container, false);
        sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        swipeRefresh = view.findViewById(R.id.swiperefresh_anotherUserProfileFragment);
        userName = AnotherUserProfileFragmentArgs.fromBundle(getArguments()).getUserName();

        swipeRefresh.setOnRefreshListener(() -> {
            Model.instance.refreshProfilePostsList(userName, "myPosts");
        });


        viewModel.setPostsData_list(userName, "myPosts");


        image_profile = view.findViewById(R.id.circleImageProfile_anotherUserProfileFragment);

        profileName = view.findViewById(R.id.profileName_another_UserProfileFragment_tv);
        HashMap<String, String> map = new HashMap<>();

        map.put("userName", userName);
        Model.instance.checkIfUserExist(map, (user) -> {
            username = user.getUserName();
            profileName.setText(user.getUserName());
            new ProfileFragment.getImageFromUrl(image_profile).execute(user.getImageUrl());

        });


        postsViewPager = view.findViewById(R.id.postViewPager_anotherUserProfileFragment);
        adapter = new MyAdapter();
        postsViewPager.setAdapter(adapter);

        setHasOptionsMenu(true);
        viewModel.getPostsData_list().observe(getViewLifecycleOwner(), list -> refresh());
        swipeRefresh.setRefreshing(Model.instance.getPostsProfileListLoadingState().getValue() == Model.PostsListLoadingState.loading);
        Model.instance.getPostsProfileListLoadingState().observe(getViewLifecycleOwner(), new Observer<Model.PostsListLoadingState>() {
            @Override
            public void onChanged(Model.PostsListLoadingState postsListLoadingState) {
                if (postsListLoadingState == Model.PostsListLoadingState.loading) {
                    swipeRefresh.setRefreshing(true);
                } else {
                    swipeRefresh.setRefreshing(false);
                }
            }
        });

        number_of_posts = view.findViewById(R.id.number_of_posts_another_profileFragment_tv);
        number_of_followers = view.findViewById(R.id.number_of_followers_another_progileFragment_tv);
        number_of_following = view.findViewById(R.id.number_of_following_another_profileFragment_tv);
        Model.instance.getMyPostsLocations(userName, locations -> {
            if (locations != null) {
                int num_of_posts = locations.size();
                number_of_posts.setText(String.valueOf(num_of_posts));
            }
        });

        Model.instance.getFollowersByUserName(userName, users -> {
            if (users != null) {
                int num_followers = users.size();
                number_of_followers.setText(String.valueOf(num_followers));
            }
        });

        Model.instance.getFollowingByUserName(userName, following -> {
            if (following != null) {
                int num_following = following.size();
                number_of_following.setText(String.valueOf(num_following));
            }
        });

        number_of_followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(AnotherUserProfileFragmentDirections.actionAnotherUserProfileFragmentToFollowFragment("Followers", userName));
            }
        });

        number_of_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(AnotherUserProfileFragmentDirections.actionAnotherUserProfileFragmentToFollowFragment("Following", userName));
            }
        });

        return view;
    }

    public static Bitmap toBitmap(String imageUrl) {
        byte[] data = Base64.decode(imageUrl, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(data, 0, data.length);
        return decodedImage;
    }

    private void refresh() {
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title_tv;
        VideoView videoView;
        TextView username_tv;
        TextView description_tv;
        ImageButton edit_btn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title_tv = itemView.findViewById(R.id.post_item_title_tv);
            videoView = itemView.findViewById(R.id.test_to_like_btn);
            username_tv = itemView.findViewById(R.id.post_item_profileName_tv);
            description_tv = itemView.findViewById(R.id.post_item_description_tv);
            progressBar = itemView.findViewById(R.id.post_item_progressBar);
            edit_btn = itemView.findViewById(R.id.ibtn_editpost_post_item_to_profile);
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.post_item_to_fragment_profile, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Post post = viewModel.getPostsData_list().getValue().get(position);
            holder.title_tv.setText(post.getPostTitle());
            holder.title_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view).navigate((NavDirections) AnotherUserProfileFragmentDirections.actionAnotherUserProfileFragmentToPostDetailsFragment2(
                            post.getPostID()));
                }
            });
            holder.videoView.setVideoPath(post.getVideoUrl());
            post_id = post.getPostID();
            holder.description_tv.setText(post.getPostDescription());
            holder.edit_btn.setVisibility(View.GONE);
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
                    if (viewModel.getPostsData_list().getValue().size() <= holder.getAdapterPosition() + 1) {
                        adapter.bindViewHolder(holder, 0);
                    } else {
                        adapter.bindViewHolder(holder, holder.getAdapterPosition() + 1);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (viewModel.getPostsData_list().getValue() == null) {
                return 0;
            }
            return viewModel.getPostsData_list().getValue().size();
        }
    }

}


