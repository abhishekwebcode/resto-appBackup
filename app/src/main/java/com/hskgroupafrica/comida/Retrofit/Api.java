package com.hskgroupafrica.comida.Retrofit;

import retrofit.RestAdapter;
import retrofit.android.AndroidLog;

/**
 * Created by AbhiAndroid
 */
public class Api {
    public static ApiInterface getClient() {
        //final OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(60,TimeUnit.SECONDS).writeTimeout(60,TimeUnit.SECONDS).build();
        // change your base URL
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint("https://comidaghana.com/food_app/JSON/API") //Set the Root URL
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog("RETROFIT LOG"))
                //.setClient(new OkClient(okHttpClient))
                //  .setEndpoint("http://abhidemo.com") //Set the Root URL
                .build(); //Finally building the adapter
        //Creating object for our interface
        ApiInterface api = adapter.create(ApiInterface.class);
        return api;
    }
}