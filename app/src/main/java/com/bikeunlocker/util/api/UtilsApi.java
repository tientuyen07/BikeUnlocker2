package com.bikeunlocker.util.api;

/**
 * Created by Administrator on 10/25/2017.
 */

public class UtilsApi {
    //    public static final String BASE_URL_API = "http://bikeunlocker.000webhostapp.com/bikeunlocker/";
//    public static final String BASE_URL_API = "http://192.168.0.113/mahasiswa/"; // mang o lab
    public static final String BASE_URL_API = "http://192.168.1.32/mahasiswa/"; // mang o nha
//    public static final String BASE_URL_API = "http://192.168.173.2/mahasiswa/";

    public static BaseApiService getApiService() {
        return RetrofitClient.getClient(BASE_URL_API).create(BaseApiService.class);
    }
}
