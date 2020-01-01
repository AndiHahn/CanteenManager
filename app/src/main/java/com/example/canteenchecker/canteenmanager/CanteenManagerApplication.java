package com.example.canteenchecker.canteenmanager;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class CanteenManagerApplication extends Application {

    private static final String FIREBASE_MESSAGING_TOPIC = "canteens";

    private String authToken = null;

    private static CanteenManagerApplication instance = null;

    public static CanteenManagerApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        //subscribe to FCM topic -> get push notifications
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic(FIREBASE_MESSAGING_TOPIC);
    }

    public synchronized String getAuthToken() {
        return authToken;
    }

    public synchronized void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public synchronized void deleteAuthToken() { this.authToken = null; }

    public synchronized boolean isAuthenticated() {
        return getAuthToken() != null;
    }
}
