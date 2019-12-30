package com.example.canteenchecker.canteenmanager.proxy;

import android.util.Log;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
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
import retrofit2.http.PUT;
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

    public Canteen getAdminCanteen(String authToken) throws IOException {
        ProxyCanteen canteen = proxy.getAdminCanteen(String.format("Bearer %s", authToken)).execute().body();
        return canteen != null ? canteen.toCanteen() : null;
    }

    public String updateAdminCanteen(String authToken, Canteen canteen) throws IOException {
        proxy.putAdminCanteen(String.format("Bearer %s", authToken), new ProxyCanteen(canteen)).execute().body();
        return "Successful";
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

        @GET("/Admin/Canteen")
        Call<ProxyCanteen> getAdminCanteen(@Header("Authorization") String authenticationToken);

        @PUT("/Admin/Canteen")
        Call<Void> putAdminCanteen(@Header("Authorization") String authenticationToken, @Body ProxyCanteen canteen);

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

    private static class ProxyCanteen {
        final int canteenId;
        final String name;
        final String meal;
        final float mealPrice;
        final String address;
        final String website;
        final String phone;
        final float averageRating;
        final int averageWaitingTime;

        ProxyCanteen(Canteen canteen) {
            this.canteenId = Integer.parseInt(canteen.getId());
            this.name = canteen.getName();
            this.phone = canteen.getPhoneNumber();
            this.website = canteen.getWebsite();
            this.meal = canteen.getSetMeal();
            this.mealPrice = canteen.getSetMealPrice();
            this.averageRating = canteen.getAverageRating();
            this.address = canteen.getLocation();
            this.averageWaitingTime = canteen.getAverageWaitingTime();

            /*
            Log.e("FUD", String.valueOf(this.canteenId));
            Log.e("FUD", this.name);
            Log.e("FUD", this.phone);
            Log.e("FUD", this.website);
            Log.e("FUD", this.meal);
            Log.e("FUD", String.valueOf(this.mealPrice));
            Log.e("FUD", String.valueOf(this.averageRating));
            Log.e("FUD", this.address);
            Log.e("FUD", String.valueOf(this.averageWaitingTime));

             */
        }

        Canteen toCanteen() {
            return new Canteen(String.valueOf(canteenId), name, meal, mealPrice, address,
                               website, phone, averageRating, averageWaitingTime);
        }

    }
}
