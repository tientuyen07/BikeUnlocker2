package com.bikeunlocker.util.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Administrator on 10/25/2017.
 */

public interface BaseApiService {

    @FormUrlEncoded // Ma hoa du lieu truoc khi POST
    @POST("login.php")
    Call<ResponseBody> loginRequest(@Field("email") String email,
                                    @Field("password") String password);

    @FormUrlEncoded
    @POST("register.php")
    Call<ResponseBody> registerRequest(@Field("name") String name,
                                       @Field("email") String email,
                                       @Field("password") String password);

    @FormUrlEncoded
    @POST("location.php")
    Call<ResponseBody> locationRequest(@Field("latitude") String latitude,
                                       @Field("longitude") String longitude);
}



