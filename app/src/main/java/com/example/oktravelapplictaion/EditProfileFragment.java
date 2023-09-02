package com.example.oktravelapplictaion;

import static android.app.Activity.RESULT_OK;

import static com.example.oktravelapplictaion.ProfileFragment.toBitmap;
import static com.example.oktravelapplictaion.login.SignUpFragment.convertBitmapToString;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.oktravelapplictaion.feed.MainActivity;
import com.example.oktravelapplictaion.model.Model;
import com.example.oktravelapplictaion.model.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class EditProfileFragment extends Fragment {

    Button save_btn;
    Button cancel_btn;
    EditText userName_et;
    EditText phone_et;
    ShapeableImageView image_profile;
    ProgressBar progressBar_pb;
    ImageButton gallary_ibtn;
    ImageButton camera_ibtn;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String oldUserName;
    String oldImage;
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        oldUserName = EditProfileFragmentArgs.fromBundle(getArguments()).getOldUserName();
        userName_et = view.findViewById(R.id.userName_editProfile_fragment_et);
        phone_et = view.findViewById(R.id.phone_editProfile_fragment_et);
        image_profile = view.findViewById(R.id.circleImageProfile_editprofileFragment);
        progressBar_pb = view.findViewById(R.id.progressBar_editProfile_fragment_pb);
        progressBar_pb.setVisibility(View.GONE);
        HashMap<String,String> map = new HashMap<>();
        map.put("userName",sp.getString("userName",""));
        Model.instance.checkIfUserExist(map,(user)-> {
            userName_et.setText(user.getUserName());
            phone_et.setText(user.getPhoneNumber());
            if(user.getImageUrl()!=null){
                oldImage = user.getImageUrl();
                /// fix
                new ProfileFragment.getImageFromUrl(image_profile).execute(user.getImageUrl());

            }
        });

        save_btn = view.findViewById(R.id.save_editProfile_fragment_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(oldUserName.equals(userName_et.getText().toString()))
                {
                    save();
                }
                else{
                    HashMap<String,String> check_map = new HashMap<>();
                    check_map.put("userName",userName_et.getText().toString());
                    Model.instance.checkIfUserExist(check_map,user -> {
                        if(user==null)
                            save();
                        else{
                            Toast.makeText(getContext(), "username already exists", Toast.LENGTH_LONG).show();
                        }
                    });
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
                openGallery();
            }
        });

        return view;
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

    private void save() {
        progressBar_pb.setVisibility(View.VISIBLE);
        save_btn.setEnabled(false);
        cancel_btn.setEnabled(false);

        HashMap<String,String> userMap = new HashMap<>();
        userMap.put("oldUserName", oldUserName);
        userMap.put("userName", userName_et.getText().toString());
        userMap.put("phoneNumber",phone_et.getText().toString());
        String imageUrl;
        if(imageBitmap != null){
            try{
                imageUrl = convertBitmapToString(imageBitmap);
                String path = Environment.getExternalStorageDirectory().toString();
                OutputStream fOut = null;
                Integer counter = 0;
                File file = new File(path, "profilePiture" + counter + ".jpg");
                fOut = new FileOutputStream(file);

                Bitmap pictureBitmap = imageBitmap; // obtaining the Bitmap
                pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                fOut.flush(); // Not really required
                fOut.close(); // do not forget to close the stream

                Model.instance.uploadImage(file,image_url -> {
                    String imageUrlFromServer=Model.COLMAN_SERVER_URL+image_url;
                    userMap.put("imageUrl",imageUrlFromServer);
                    Model.instance.editUser(userMap, () ->{
                        sp.edit().putString("userName", userName_et.getText().toString()).apply();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    });
                });

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        else{
            imageUrl = oldImage;
            userMap.put("imageUrl",imageUrl);
            Model.instance.editUser(userMap, () ->{
                sp.edit().putString("userName", userName_et.getText().toString()).apply();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            });
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








