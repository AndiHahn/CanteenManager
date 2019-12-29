package com.example.canteenchecker.canteenmanager.proxy;

import com.example.canteenchecker.canteenmanager.core.Canteen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class ServiceProxy {

    // ADMIN --> https://canteencheckeradmin.azurewebsites.net/
    // Test user: S23423432/S23423432
    // SWAGGER --> https://canteenchecker.azurewebsites.net/swagger/ui/index
    private static final String SERVICE_BASE_URL = "https://canteenchecker.azurewebsites.net/";

    private final Proxy proxy = new Retrofit.Builder()
            .baseUrl(SERVICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Proxy.class);

    public String authenticate(String userName, String password) throws IOException {
        return proxy.postLogin(new ProxyLogin(userName, password)).execute().body();
    }

    private interface Proxy {

        //@GET("/Public/Canteen")
        //Call<Collection<ProxyCanteen>> getCanteens(@Query("nameFilter") String filter);

        //@GET("/Public/Canteen/{id}")
        //Call<ProxyCanteen> getCanteen(@Path("id") String canteenId);

        //@GET("/Public/Canteen/{id}/Rating?nrOfRatings=0")
        //Call<ProxyReviewData> getReviewDataForCanteen(@Path("id") String canteenId);

        @POST("/Admin/Login")
        Call<String> postLogin(@Body ProxyLogin login);

        //@POST("/Admin/Canteen/Rating")
        //Call<ProxyRating> postRating(@Header("Authorization") String authenticationToken, @Body ProxyNewRating rating);

    }

    private static class ProxyLogin {

        final String username;
        final String password;

        ProxyLogin(String userName, String password) {
            this.username = userName;
            this.password = password;
        }

    }
}
