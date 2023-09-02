package com.example.oktravelapplictaion.login;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.oktravelapplictaion.R;
import com.example.oktravelapplictaion.feed.MainActivity;
import com.example.oktravelapplictaion.model.Model;
import com.example.oktravelapplictaion.model.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpFragment extends Fragment {

    static final int CAMERA_PERMISSION_CODE = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    Button submit_btn;
    Button cancel_btn;
    EditText userName_et;
    EditText password_et;
    EditText verifyPassword_et;
    EditText email_et;
    EditText phone_et;
    ShapeableImageView image_profile;
    ProgressBar progressBar_pb;
    ImageButton gallary_ibtn;
    ImageButton camera_ibtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        userName_et = view.findViewById(R.id.userName_editProfile_fragment_et);
        password_et = view.findViewById(R.id.password_editProfile_fragment_et);
        verifyPassword_et = view.findViewById(R.id.verifyPassword_signup_fragment_et);
        email_et = view.findViewById(R.id.email_signup_fragment_et);
        phone_et = view.findViewById(R.id.phone_editProfile_fragment_et);
        image_profile = view.findViewById(R.id.circleImageProfile_signupFragment);

        progressBar_pb = view.findViewById(R.id.progressBar_editProfile_fragment_pb);
        progressBar_pb.setVisibility(View.GONE);

        submit_btn = view.findViewById(R.id.save_editProfile_fragment_btn);
        submit_btn.setEnabled(false);
        setFocus();
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean details_status = check();

                if (details_status) {
                    save();
                }

            }
        });
        cancel_btn = view.findViewById(R.id.cencel_editProfile_fragment_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
                setHasOptionsMenu(true);
            }
        });

        gallary_ibtn = view.findViewById(R.id.gallery_editProfile_fragment_ibtn);
        camera_ibtn = view.findViewById(R.id.camera_editProfile_fragment_ibtn);

        camera_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        gallary_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCameraPermission();
                openGallery();
            }
        });

        return view;
    }

  
    private void getCameraPermission (){
        if(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this.getActivity(), new String[]
                    {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }


    public void setFocus(){
        userName_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    HashMap<String,String> map = new HashMap<>();
                    map.put("userName",userName_et.getText().toString());
                    Model.instance.checkIfUserExist(map, new Model.GetUserByUserNameByListener() {
                        @Override
                        public void onComplete(User user) {
                            if (user != null) {
                                Toast.makeText(getContext(), "username already exist", Toast.LENGTH_LONG).show();
                                submit_btn.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean check() {
        if (userName_et.getText().toString().isEmpty() || password_et.getText().toString().isEmpty() ||
        verifyPassword_et.getText().toString().isEmpty() || email_et.getText().toString().isEmpty() ||
        phone_et.getText().toString().isEmpty() || imageBitmap == null)
        {
            Toast.makeText(getContext(), "one of the details empty", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(password_et.getText().toString().length()< 6){
            Toast.makeText(getContext(), "password should contain at least 6 characters", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(!(password_et.getText().toString().equals(verifyPassword_et.getText().toString()))){
            Toast.makeText(getContext(), "password and verification not equal", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (userName_et.getText().toString().contains(" ") || password_et.getText().toString().contains(" ") ||
                verifyPassword_et.getText().toString().contains(" ") || email_et.getText().toString().contains(" ") ||
                phone_et.getText().toString().contains(" ")){
            return false;
        }
        else if(!(email_et.getText().toString().contains("@"))){
            Toast.makeText(getContext(), "wrong email", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(!(phone_et.getText().toString().matches("[0-9]+")))
        {
            Toast.makeText(getContext(), "phone number should contain only numbers", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(imageBitmap == null){
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
                image_profile.setImageBitmap(imageBitmap);
            }
        }
    }
    public static String convertBitmapToString(Bitmap imageBitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        String encoded = Base64.encodeToString(data, Base64.DEFAULT);
        return encoded;
    }

    private void save() {
        progressBar_pb.setVisibility(View.VISIBLE);
        submit_btn.setEnabled(false);
        cancel_btn.setEnabled(false);
        String userName = userName_et.getText().toString();
        String password = password_et.getText().toString();
        String email = email_et.getText().toString();
        String phone = phone_et.getText().toString();
        String profilePicture = "";

        User user = new User(userName, password, email, phone, profilePicture);

        if(imageBitmap != null) {

            try {
                String imageUrl = convertBitmapToString(imageBitmap);
                String path = Environment.getExternalStorageDirectory().toString();
                OutputStream fOut = null;
                Integer counter = 0;
                File file = new File(path, "profilePiture" + counter + ".jpg");
                fOut = new FileOutputStream(file);


                Bitmap pictureBitmap = imageBitmap; // obtaining the Bitmap
                pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                fOut.flush(); // Not really required
                fOut.close(); // do not forget to close the stream

                Model.instance.uploadImage(file,image_url->{
                    String imageUrlFromServer=Model.COLMAN_SERVER_URL+image_url;
                    user.setProfileImageUrl(imageUrlFromServer);
                    Log.d("TAGGGGGG",user.getImageUrl());
                    Model.instance.addUser(user,() -> {
                        Navigation.findNavController(userName_et).navigateUp();
                    });

                    System.out.printf(imageUrlFromServer);
                });

            }
            catch (IOException e){
                e.printStackTrace();
            }

        }

    }

    private void openCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    private void openGallery() {
        Intent chooseImageIntent = new Intent();
        chooseImageIntent.setType("image/*");
        chooseImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(chooseImageIntent,"select image"),REQUEST_IMAGE_CAPTURE);
    }
}