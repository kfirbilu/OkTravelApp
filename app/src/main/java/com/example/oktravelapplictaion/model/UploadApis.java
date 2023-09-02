package com.example.oktravelapplictaion.model;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadApis {
//RequestBody
    //ResponseBody
    @Multipart
    @POST("/upload")
    Call<String> uploadVideo(@Part MultipartBody.Part part, @Part("somedata") RequestBody requestBody );
}

