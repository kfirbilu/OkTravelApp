package com.example.oktravelapplictaion;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oktravelapplictaion.model.Model;
import com.example.oktravelapplictaion.model.User;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.HashMap;
import java.util.List;


public class FriendsFragment extends Fragment {
    ImageButton search_btn;
    EditText username_et;
    SharedPreferences sp;
    List<User> data;
    MyAdapter adapter;
    private String access_token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        access_token= "Bearer "+sp.getString("accessToken","");
        RecyclerView list = view.findViewById(R.id.results_rv_friends_fragment);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyAdapter();
        list.setAdapter(adapter);
        setHasOptionsMenu(true);
        username_et = view.findViewById(R.id.username_et_friends_fragment);
        search_btn = view.findViewById(R.id.search_btn_friends_fragmnet);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFunc();
            }
        });
        return view;
    }

    private void searchFunc(){
        HashMap<String,String> map = new HashMap<>();
        map.put("userName",username_et.getText().toString());
        Model.instance.partialSearch(map,users -> {
            data = users;
            if(data.size()==0){
                Toast.makeText(getContext(), "No results", Toast.LENGTH_LONG).show();
            }
            adapter.notifyDataSetChanged();
        });

    }
    public HashMap<String,String> setMap(String userName){
        String from = sp.getString("userName", "");
//        String to = username_et.getText().toString();
        HashMap<String, String> map = new HashMap<>();
        map.put("from", from);
        map.put("to", userName);
        return map;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nameTv;
        ShapeableImageView image_friend;
        Button follow_btn;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.name_tv_friend_row);
            image_friend = itemView.findViewById(R.id.image_friend_row);
            follow_btn = itemView.findViewById(R.id.follow_ibtn);
        }
    }

    interface OnItemClickListener{
        void onItemClick(View v,int position);
    }
    class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        OnItemClickListener listener;
        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.follow_row,parent,false);
            MyViewHolder holder = new MyViewHolder(view,listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            User user = data.get(position);
            holder.nameTv.setText(user.getUserName());
            if(position % 2 == 0)
                holder.itemView.setBackgroundResource(R.drawable.background_all_friends);
            new ProfileFragment.getImageFromUrl(holder.image_friend).execute(user.getImageUrl());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(v).navigate(TabsSearchFragmentDirections.actionTabsSearchFragmentToAnotherUserProfileFragment(user.getUserName()));
                }
            });

            if(sp.getString("userName", "").equals(user.getUserName())) {
                holder.follow_btn.setVisibility(View.GONE);
                holder.image_friend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Navigation.findNavController(v).navigate(TabsSearchFragmentDirections.actionTabsSearchFragmentToProfileFragment());
                    }
                });
            }
            else {
                holder.follow_btn.setVisibility(View.VISIBLE);
                holder.image_friend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Navigation.findNavController(v).navigate(TabsSearchFragmentDirections.actionTabsSearchFragmentToAnotherUserProfileFragment(user.getUserName()));
                    }
                });
                Model.instance.checkIfFollow(setMap(user.getUserName()), (found) -> {
                    if (found) {
                        holder.follow_btn.setText("unfollow");
                    } else {
                        holder.follow_btn.setText("follow");
                    }
                });
            }
            holder.follow_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.follow_btn.getText().toString().equals("follow")) {
                        Model.instance.follow(setMap(user.getUserName()), access_token, () -> {
                            holder.follow_btn.setText("unfollow");
                        });
                    }
                    else{
                        Model.instance.unfollow(setMap(user.getUserName()), access_token, ()->{
                            holder.follow_btn.setText("follow");
                        });
                    }
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
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            return super.onOptionsItemSelected(item);
    }
}