package com.example.oktravelapplictaion;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.oktravelapplictaion.model.Model;
import com.example.oktravelapplictaion.model.Post;

import java.io.File;
import java.util.Arrays;

public class fragment_edit_post extends Fragment {
    static final int REQUEST_VIDEO_CAPTURE = 1;
    static final int VIDEO_RECORD_CODE = 101;

    VideoView video;
    Button upload_btn;
    Button delete_btn;
    EditText title_et;
    EditText description_et;
    Uri videoSelected;
    CheckBox forFamilies_cb;
    CheckBox waterTrails_cb;
    CheckBox food_cb;
    CheckBox romantic_cb;
    CheckBox kids_cb;
    String post_id;
    CheckBox viewPoint_cb;
    CheckBox springs_cb;
    CheckBox picnic_cb;
    ImageButton location_edit_btn;
    ProgressBar progressBar;
    ImageButton add_btn, record_btn;
    float longitude, longitude_frommap;
    float latitude, latitude_frommap;
    boolean flag_clicked_location = false;
    SharedPreferences sp;
    String access_token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_post, container, false);
        sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        post_id = fragment_edit_postArgs.fromBundle(getArguments()).getPostId();
        add_btn = view.findViewById(R.id.add_video_btn_editpost_fragment);
        record_btn = view.findViewById(R.id.record_btn_editpost_fragment);
        upload_btn = view.findViewById(R.id.upload_btn_editpost_fragment);
        delete_btn = view.findViewById(R.id.delete_btn_editpost_fragment);
        video = view.findViewById(R.id.video_editpost_fragment);
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        title_et = view.findViewById(R.id.title_editpost_fragment);
        description_et = view.findViewById(R.id.description_editpost_fragment);
        forFamilies_cb = view.findViewById(R.id.families_cb_editpost_fragment);
        waterTrails_cb = view.findViewById(R.id.water_cb_editpost_fragment);
        food_cb = view.findViewById(R.id.food_cb_editpost_fragment);
        romantic_cb = view.findViewById(R.id.romantic_cb_editpost_fragment);
        kids_cb = view.findViewById(R.id.kids_cb_editpost_fragment);
        viewPoint_cb = view.findViewById(R.id.viewpoint_cb_editpost_fragment);
        springs_cb = view.findViewById(R.id.spring_cb_editpost_fragment);
        picnic_cb = view.findViewById(R.id.picnic_cb_editpost_fragment);
        location_edit_btn = view.findViewById(R.id.location_imagebtn_editpost_fragment);
        progressBar = view.findViewById(R.id.progressBar_edit_post_fragment_pb);
        progressBar.setVisibility(View.GONE);
        access_token = "Bearer " + sp.getString("accessToken", "");
        Model.instance.getPostById(post_id, post -> {
            title_et.setText(post.getPostTitle());
            description_et.setText(post.getPostDescription());
            video.setVideoPath(post.getVideoUrl());
            video.start();
            String[] categories = post.getCategoriesList().split(",");
            checkCategoriesList(categories);
            longitude = post.getLongitude();
            latitude = post.getLatitude();
            longitude_frommap = fragment_edit_postArgs.fromBundle(getArguments()).getLongitude();
            latitude_frommap = fragment_edit_postArgs.fromBundle(getArguments()).getLatitude();
            if (longitude_frommap != 0.0f && latitude_frommap != 0.0f) {
                longitude = longitude_frommap;
                latitude = latitude_frommap;
            }

        });
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (longitude == 0 && latitude == 0) {
                    Toast.makeText(getContext(), "First you need set location", Toast.LENGTH_LONG).show();
                } else {
                    openGallery();
                }
            }
        });
        record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (longitude == 0 && latitude == 0) {
                    Toast.makeText(getContext(), "First you need set location", Toast.LENGTH_LONG).show();
                } else {
                    recordVideoButtonPressed(view);
                }
            }
        });
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPost(latitude, longitude, videoSelected, title_et.getText().toString(),
                        description_et.getText().toString())) {
                    UploadPost();
                } else {
                    Toast.makeText(getContext(), "Must fill all fields", Toast.LENGTH_LONG).show();
                }
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeletePost();
            }
        });

        location_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag_clicked_location = true;
                Model.isFromEditPost = true;
                Navigation.findNavController(v).navigate(fragment_edit_postDirections.actionFragmentEditPostToMapFragment().setLatitude(latitude).setLongitude(longitude).setPostId(post_id));
            }
        });
        return view;
    }

    public void recordVideoButtonPressed(View view) {
        recordVideo();
    }

    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, VIDEO_RECORD_CODE);
    }

    private void openGallery() {
        Intent chooseVideoIntent = new Intent();
        chooseVideoIntent.setType("video/*");
        chooseVideoIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(chooseVideoIntent, "select video"), REQUEST_VIDEO_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                videoSelected = data.getData();
                if (videoSelected != null) {
                    video.setVideoURI(videoSelected);
                    video.start();
                }
            }
        }
        if (requestCode == VIDEO_RECORD_CODE) {
            if (resultCode == RESULT_OK) {
                videoSelected = data.getData();
                if (videoSelected != null) {
                    video.setVideoURI(videoSelected);
                    video.start();

                }
                Log.i("VIDEO_RECORD_TAG", "Video is recorded and available at path " + videoSelected);
            } else if (resultCode == RESULT_CANCELED) {
                Log.i("VIDEO_RECORD_TAG", "Recording video is cancelled");
            } else {
                Log.i("VIDEO_RECORD_TAG", "Recording video has got some error");
            }

        }


    }

    public void UploadPost() {
        progressBar.setVisibility(View.VISIBLE);
        upload_btn.setEnabled(false);
        add_btn.setEnabled(false);
        delete_btn.setEnabled(false);
        String postTitle = title_et.getText().toString();
        String postDescription = description_et.getText().toString();
        String checkBoxList = checkCheckboxes();
        Post post = new Post(postTitle, postDescription, sp.getString("userName", ""), 0, checkBoxList, null, longitude, latitude);
        post.setPostID(post_id);


        if (videoSelected != null) {
            String videoNameFixed = "";
            if (videoSelected.getPath().startsWith("/document/raw:")) {
                videoNameFixed = videoSelected.getPath().replace("/document/raw:", "");
            } else {
                videoNameFixed = getFullPathFromContentUri(getActivity(), videoSelected);
            }
            File correctFile = new File(videoNameFixed);
            Model.instance.uploadVideo(correctFile, video_url1 -> {
                String videoUrlFromServer = Model.COLMAN_SERVER_URL + video_url1;
                post.setVideoUrl(videoUrlFromServer);
                Model.instance.editPost(post, access_token, () -> {
                    Navigation.findNavController(upload_btn).navigate(R.id.action_global_feedFragment);
                });

            });
        } else {
            Model.instance.getPostById(post_id, post1 -> {
                post.setVideoUrl(post1.getVideoUrl());
                Model.instance.editPost(post, access_token, () -> {
                    Navigation.findNavController(upload_btn).navigate(R.id.action_global_feedFragment);
                });
            });
        }
    }

    public void checkCategoriesList(String[] categories) {
        if (Arrays.asList(categories).contains("forFamilies")) {
            forFamilies_cb.setChecked(true);
        }
        if (Arrays.asList(categories).contains("waterTrails")) {
            waterTrails_cb.setChecked(true);
        }
        if (Arrays.asList(categories).contains("food")) {
            food_cb.setChecked(true);
        }
        if (Arrays.asList(categories).contains("romantic")) {
            romantic_cb.setChecked(true);
        }
        if (Arrays.asList(categories).contains("kids")) {
            kids_cb.setChecked(true);
        }
        if (Arrays.asList(categories).contains("viewPoint")) {
            viewPoint_cb.setChecked(true);
        }
        if (Arrays.asList(categories).contains("springs")) {
            springs_cb.setChecked(true);
        }
        if (Arrays.asList(categories).contains("picnic")) {
            picnic_cb.setChecked(true);
        }
    }

    public String checkCheckboxes() {
        StringBuilder checkBoxlist = new StringBuilder();
        if (forFamilies_cb.isChecked())
            checkBoxlist.append("forFamilies,");
        if (waterTrails_cb.isChecked())
            checkBoxlist.append("waterTrails,");
        if (food_cb.isChecked())
            checkBoxlist.append("food,");
        if (romantic_cb.isChecked())
            checkBoxlist.append("romantic,");
        if (kids_cb.isChecked())
            checkBoxlist.append("kids,");
        if (viewPoint_cb.isChecked())
            checkBoxlist.append("viewPoint,");
        if (springs_cb.isChecked())
            checkBoxlist.append("springs,");
        if (picnic_cb.isChecked())
            checkBoxlist.append("picnic");
        return checkBoxlist.toString();
    }

    public void DeletePost() {
        progressBar.setVisibility(View.VISIBLE);
        upload_btn.setEnabled(false);
        add_btn.setEnabled(false);
        delete_btn.setEnabled(false);
        Model.instance.deletePost(post_id, access_token, () -> {
            Navigation.findNavController(upload_btn).navigate(R.id.action_global_feedFragment);
        });

    }

    public boolean checkPost(float latitude, float longitude, Uri videoSelected, String title, String description) {
        if (latitude == 0 ||
                longitude == 0 ||
                title.equals("") ||
                description.equals("")) {
            Log.d("TAG", "************" + latitude + "****" + longitude + "******" + videoSelected + "********" + title + "****" + description);
            return false;
        }
        return true;
    }


    public static String getFullPathFromContentUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }//non-primary e.g sd card
                else {
                    String filePath = "";
                    if (Build.VERSION.SDK_INT > 20) {
                        //getExternalMediaDirs() added in API 21
                        File extenal[] = context.getExternalMediaDirs();
                        for (File f : extenal) {
                            filePath = f.getAbsolutePath();
                            if (filePath.contains(type)) {
                                int endIndex = filePath.indexOf("Android");
                                filePath = filePath.substring(0, endIndex) + split[1];
                            }
                        }
                    } else {
                        filePath = "/storage/" + type + "/" + split[1];
                    }
                    return filePath;
                }
            }
            // DownloadsProvider
            else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                Cursor cursor = null;
                final String column = "_data";
                final String[] projection = {
                        column
                };

                try {
                    cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                            null);
                    if (cursor != null && cursor.moveToFirst()) {
                        final int column_index = cursor.getColumnIndexOrThrow(column);
                        return cursor.getString(column_index);
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
                return null;
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


}