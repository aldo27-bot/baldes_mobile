package com.ELayang.Desa.API;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroServer {
    //    public static String server = "https://elades.pbltifnganjuk.com";
    public static String server = "http://10.0.2.2/si-kunir-web";
    //    private static final String baseURL = "http://" + server + "/coding/ELaDes%20WEB/DatabaseMobile/";
    private static final String finalurl = server + "/DatabaseMobile/";
    public static final String API_IMAGE = server + "/uploads/";
    public static final String API_FotoProfil = server + "/";
    private static Retrofit retro;
    //    public static final String API_IMAGE = finalurl + "/elades/uploads/";
    public String getUrlImage(){
        return API_IMAGE;
    }
    public static Retrofit konekRetrofit() {
        if (retro == null) {
            // Tambahkan logging interceptor
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            // Inisialisasi Retrofit dengan interceptor
            retro = new Retrofit.Builder()
                    .baseUrl(finalurl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retro;
    }

}