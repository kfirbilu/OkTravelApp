package com.example.oktravelapplictaion.login;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oktravelapplictaion.R;
import com.example.oktravelapplictaion.feed.MainActivity;
import com.example.oktravelapplictaion.model.Model;
import com.example.oktravelapplictaion.model.User;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;
import java.util.HashMap;


public class FB_Missing_Data_Fragment extends Fragment {
    private EditText phoneNumber;
    public TextView email_got , username_got;
    ProgressBar progressBar_pb;
    Button continue_btn, cancel_btn;
    String fb_email, fb_username, userId, imageUrl, password_got;
    ProfilePictureView pic_got;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    SharedPreferences sp;
    ShapeableImageView image_profile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_f_b__missing__data, container, false);
        fb_email = FB_Missing_Data_FragmentArgs.fromBundle(getArguments()).getFbEmail();
        userId = FB_Missing_Data_FragmentArgs.fromBundle(getArguments()).getUserId();
        imageUrl = FB_Missing_Data_FragmentArgs.fromBundle(getArguments()).getImgaeURL();
        password_got = FB_Missing_Data_FragmentArgs.fromBundle(getArguments()).getUserPassword();
        fb_username = fb_email.split("@")[0];
        sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        email_got = view.findViewById(R.id.email_from_fb_missing_data);
        username_got = view.findViewById(R.id.username_got_from_fb);
        phoneNumber = view.findViewById(R.id.phone_missing_gmail_data);
        pic_got =(ProfilePictureView)view.findViewById(R.id.image_from_gmail_missing);
        progressBar_pb = view.findViewById(R.id.progressBar_missingData_fragment_fb);
        progressBar_pb.setVisibility(View.GONE);
        pic_got.setProfileId(userId);
        email_got.setText(fb_email);
        username_got.setText(fb_username);
        continue_btn = view.findViewById(R.id.continue_missing_gmail_data_Btn);
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean details_status = check();
                if (details_status) {
                    save();
                }
            }
        });
        cancel_btn = view.findViewById(R.id.cancel_missing_gmail_data_Btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                Navigation.findNavController(v).navigateUp();
                setHasOptionsMenu(true);
            }
        });
        return view;
    }

    private boolean check() {
        if (pic_got == null ||
                phoneNumber.getText().toString().isEmpty() )
        {
            Toast.makeText(getContext(), "one of the details empty", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (fb_username.contains(" ") ||
                phoneNumber.getText().toString().contains(" ")){
            return false;
        }
        else if(!(phoneNumber.getText().toString().matches("[0-9]+")))
        {
            Toast.makeText(getContext(), "phone number should contain only numbers", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(!(email_got.getText().toString().contains("@"))){
            Toast.makeText(getContext(), "wrong email", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(pic_got == null){
            Toast.makeText(getContext(), "please choose an image profile", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    Bitmap imageBitmap;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            if(resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                if (extras == null){
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    imageBitmap = (Bitmap) extras.get("data");
                }
            }
        }
    }

    private void toFeedActivity() {
        sp.edit().putBoolean("logged", true).apply();
        sp.edit().putString("userName", fb_username).apply();
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void save() {
        progressBar_pb.setVisibility(View.VISIBLE);
        continue_btn.setEnabled(false);
        cancel_btn.setEnabled(false);
        String userName = fb_username;
        String password = password_got;
        String email = fb_email;
        String phone = phoneNumber.getText().toString();
        String profilePicture = "";
        User user = new User(userName, password, email, phone, profilePicture);

        user.setProfileImageUrl(imageUrl);

        Model.instance.addUser(user,() -> {
            HashMap <String,String> map = new HashMap<>();
            map.put("userName",user.getUserName());
            Model.instance.loginApps(map, tokenmap -> {
                if (tokenmap != null){
                    sp.edit().putString("accessToken",tokenmap.get("accessToken")).apply();
                    sp.edit().putString("refreshToken",tokenmap.get("refreshToken")).apply();
                    toFeedActivity();
                }
                else{
                    Toast.makeText(getContext(), "one of the details wrong", Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}