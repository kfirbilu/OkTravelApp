package com.example.oktravelapplictaion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.oktravelapplictaion.login.LoginActivity;
import com.example.oktravelapplictaion.model.Model;
import com.example.oktravelapplictaion.model.Post;


import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;


import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

public class ProfileFragment extends Fragment {

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
    Boolean liked_posts_clicked = false;
    Boolean my_posts_clicked = true;
    ShapeableImageView image_profile;
    TextView number_of_posts;
    TextView number_of_followers;
    TextView number_of_following;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel=new ViewModelProvider(this).get(ProfileViewModel.class);
        sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        if (my_posts_clicked){
            viewModel.setPostsData_list(sp.getString("userName", ""), "myPosts");
        }
        else {
            viewModel.setPostsData_list(sp.getString("userName", ""), "likedPosts");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        swipeRefresh=view.findViewById(R.id.swiperefresh_profile_fragment);

        String from_myPosts_or_likedPosts;
        if (my_posts_clicked)
            from_myPosts_or_likedPosts = "myPosts";
        else
            from_myPosts_or_likedPosts = "likedPost";

        swipeRefresh.setOnRefreshListener(() -> {
            Model.instance.refreshProfilePostsList(sp.getString("userName", ""), from_myPosts_or_likedPosts);
            my_posts.setImageResource(R.drawable.icon_myposts_clicked);
            liked_posts.setImageResource(R.drawable.ic_baseline_favorite_24);
        } );

        number_of_posts = view.findViewById(R.id.number_of_posts_profileFragment_tv);
        number_of_followers = view.findViewById(R.id.number_of_followers_progileFragment_tv);
        number_of_following = view.findViewById(R.id.number_of_following_profileFragment_tv);
        Model.instance.getMyPostsLocations(sp.getString("userName", ""), locations -> {
            if(locations != null) {
                int num_of_posts = locations.size();
                number_of_posts.setText(String.valueOf(num_of_posts));
            }
        });

        Model.instance.getFollowersByUserName(sp.getString("userName", ""), users -> {
            if (users != null) {
                int num_followers = users.size();
                number_of_followers.setText(String.valueOf(num_followers));
            }
        });

        Model.instance.getFollowingByUserName(sp.getString("userName", ""), following -> {
            if (following != null) {
                int num_following = following.size();
                number_of_following.setText(String.valueOf(num_following));
            }
        });

        number_of_followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(ProfileFragmentDirections.actionProfileFragmentToFollowFragment("Followers", sp.getString("userName", "")));
            }
        });

        number_of_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(ProfileFragmentDirections.actionProfileFragmentToFollowFragment("Following", sp.getString("userName", "")));
            }
        });


        my_posts = view.findViewById(R.id.myposts_profileFragment_ibtn);
        if (viewModel.postsData_list != null)
            my_posts.setImageResource(R.drawable.icon_myposts_clicked);
        my_posts.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                viewModel.setPostsData_list(sp.getString("userName", ""), "myPosts");
                Model.instance.refreshProfilePostsList(sp.getString("userName", ""), "myPosts");
                liked_posts_clicked = false;
                my_posts_clicked = true;
                if (viewModel.getPostsData_list().getValue().size() != 0)
                    edit_btn.setVisibility(View.VISIBLE);
                else
                    Toast.makeText(getContext(), "My posts no exist", Toast.LENGTH_LONG).show();
                my_posts.setImageResource(R.drawable.icon_myposts_clicked);
                liked_posts.setImageResource(R.drawable.ic_baseline_favorite_24);
            }
        });

        liked_posts = view.findViewById(R.id.likedPosts_profileFragment_ibtn);
        liked_posts.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                viewModel.setPostsData_list(sp.getString("userName", ""), "likedPosts");
                Model.instance.refreshProfilePostsList(sp.getString("userName", ""), "likedPosts");
                liked_posts_clicked = true;
                my_posts_clicked = false;
                if (viewModel.getPostsData_list().getValue().size() != 0)
                    edit_btn.setVisibility(View.GONE);
                else
                    Toast.makeText(getContext(), "Liked posts no exist", Toast.LENGTH_LONG).show();
                my_posts.setImageResource(R.drawable.icon_myposts);
                liked_posts.setImageResource(R.drawable.ic_baseline_favorite_24_clicked);
            }
        });
        image_profile = view.findViewById(R.id.circleImageProfile_profileFragment);

        profileName = view.findViewById(R.id.profileName_profileFragment_tv);
        HashMap<String,String> map = new HashMap<>();
        map.put("userName",sp.getString("userName",""));
        Model.instance.checkIfUserExist(map,(user)->{
            if(user != null) {
                username = user.getUserName();
                profileName.setText(user.getUserName());
                new getImageFromUrl(image_profile).execute(user.getImageUrl());
            }
        });

        edit_profile_btn = view.findViewById(R.id.edit_profileFragment_ibtn);

        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate((NavDirections) ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment(profileName.getText().toString()));
            }
        });

        logout_btn = view.findViewById(R.id.logout_profileFragment_ibtn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().putBoolean("logged", false).apply();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                LoginManager.getInstance().logOut();
                String token = "barer " + sp.getString("refreshToken", "");
                Model.instance.logOut(token, new Model.LogoutListener() {
                    @Override
                    public void onComplete() {
                        sp.edit().putBoolean("logged", false).apply();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

            }
        });



        postsViewPager = view.findViewById(R.id.postViewPager_fragment_profile);
        adapter = new MyAdapter();
        postsViewPager.setAdapter(adapter);

        setHasOptionsMenu(true);
        viewModel.getPostsData_list().observe(getViewLifecycleOwner(), list -> refresh());
        swipeRefresh.setRefreshing(Model.instance.getPostsProfileListLoadingState().getValue()== Model.PostsListLoadingState.loading);
        Model.instance.getPostsProfileListLoadingState().observe(getViewLifecycleOwner(), new Observer<Model.PostsListLoadingState>() {
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

    public static Bitmap toBitmap(String imageUrl){
        byte[] data = Base64.decode(imageUrl, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(data, 0, data.length);
        return decodedImage;
    }

    private void refresh() {
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title_tv;
        VideoView videoView;
        TextView username_tv;
        TextView description_tv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title_tv = itemView.findViewById(R.id.post_item_title_tv);
            videoView = itemView.findViewById(R.id.test_to_like_btn);
            username_tv = itemView.findViewById(R.id.post_item_profileName_tv);
            description_tv = itemView.findViewById(R.id.post_item_description_tv);
            progressBar = itemView.findViewById(R.id.post_item_progressBar);

            edit_btn=itemView.findViewById(R.id.ibtn_editpost_post_item_to_profile);
            if(liked_posts_clicked)
                edit_btn.setVisibility(View.GONE);
            else
                edit_btn.setVisibility(View.VISIBLE);
            edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String post_id= viewModel.getPostsData_list().getValue().get(postsViewPager.getCurrentItem()).getPostID();
                    Navigation.findNavController(v).navigate((NavDirections) ProfileFragmentDirections.actionProfileFragmentToFragmentEditPost(post_id));

                }
            });
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.post_item_to_fragment_profile,parent,false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Post post = viewModel.getPostsData_list().getValue().get(position);
            holder.title_tv.setText(post.getPostTitle());
            holder.videoView.setVideoPath(post.getVideoUrl());
            post_id = post.getPostID();
            holder.description_tv.setText(post.getPostDescription());

            holder.title_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(v).navigate((NavDirections) ProfileFragmentDirections.actionProfileFragmentToPostDetailsFragment2(post.getPostID()));
                }
            });

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
        }

        @Override
        public int getItemCount() {
            if(viewModel.getPostsData_list().getValue() == null){
                return 0;
            }
            return viewModel.getPostsData_list().getValue().size();
        }
    }





    // class to load pics from server async - DO NOT TOUCH!
    public static class getImageFromUrl extends AsyncTask<String, Void, Bitmap>
    {

        ShapeableImageView imageView;

        public getImageFromUrl(ShapeableImageView shapeableImageView){
            this.imageView = shapeableImageView;

        }

        @Override
        protected Bitmap doInBackground(String... src) {
            try {
                Log.d("Tag","Getting image....");
                URL url = new URL(src[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                // Log exception
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap){
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
        }

    }










}