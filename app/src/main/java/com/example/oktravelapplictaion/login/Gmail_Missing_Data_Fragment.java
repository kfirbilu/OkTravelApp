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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oktravelapplictaion.R;
import com.example.oktravelapplictaion.feed.MainActivity;
import com.example.oktravelapplictaion.model.Model;
import com.example.oktravelapplictaion.model.User;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

public class Gmail_Missing_Data_Fragment extends Fragment {
    GoogleSignInOptions gso;
    GoogleSignInClient gsc ;
    ImageView gmail_profile_pic;
    EditText phoneNumber_gmail;
    ProgressBar progressBar_gm;
    Button continue_btn_gm, cancel_btn_gm;
    String gm_email , checked_gm_email, gmail_username, userProfilePic, password_got;
    TextView email_got, username_got;
    SharedPreferences sp;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_gmail__missing_data, container, false);
        gm_email = Gmail_Missing_Data_FragmentArgs.fromBundle(getArguments()).getGmEmail();
        gmail_username = gm_email.split("@")[0];
        userProfilePic = Gmail_Missing_Data_FragmentArgs.fromBundle(getArguments()).getUserPhotoUrl();
        password_got = Gmail_Missing_Data_FragmentArgs.fromBundle(getArguments()).getUserPassword();
        sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        phoneNumber_gmail = view.findViewById(R.id.phone_missing_gmail_data);
        gmail_profile_pic = view.findViewById(R.id.image_from_gmail_missing);
        email_got = view.findViewById(R.id.gmail_email_got_missing);
        username_got = view.findViewById(R.id.user_missing_data_gm);

        progressBar_gm = view.findViewById(R.id.progressBar_missingData_fragment_gm);
        progressBar_gm.setVisibility(View.GONE);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc  = GoogleSignIn.getClient(getActivity(), gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this.getContext());
        if(acct != null){
            checked_gm_email = acct.getEmail();
        }

        Picasso.get().load(userProfilePic).into(gmail_profile_pic);
        email_got.setText(gm_email);
        username_got.setText(gmail_username);


        continue_btn_gm = view.findViewById(R.id.continue_missing_gmail_data_Btn);
        continue_btn_gm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean details_status = check();
                if (details_status) {
                    save();
                }

            }
        });
        cancel_btn_gm = view.findViewById(R.id.cancel_missing_gmail_data_Btn);
        cancel_btn_gm.setOnClickListener(new View.OnClickListener() {
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
        if (gmail_username.isEmpty() || gmail_profile_pic == null ||
                phoneNumber_gmail.getText().toString().isEmpty() )
        {
            Toast.makeText(getContext(), "one of the details empty", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (gmail_username.contains(" ") ||
                phoneNumber_gmail.getText().toString().contains(" ")){
            return false;
        }
        else if(!(phoneNumber_gmail.getText().toString().matches("[0-9]+")))
        {
            Toast.makeText(getContext(), "phone number should contain only numbers", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(!(email_got.getText().toString().contains("@"))){
            Toast.makeText(getContext(), "wrong email", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(gmail_profile_pic == null){
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
        sp.edit().putString("userName", gmail_username).apply();
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void save() {
        progressBar_gm.setVisibility(View.VISIBLE);
        continue_btn_gm.setEnabled(false);
        cancel_btn_gm.setEnabled(false);
        String userName = gmail_username;
        String password = password_got;
        String email = gm_email;
        String phone = phoneNumber_gmail.getText().toString();
        String profilePicture = "";
        User user = new User(userName, password, email, phone, profilePicture);
        user.setProfileImageUrl(userProfilePic);

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