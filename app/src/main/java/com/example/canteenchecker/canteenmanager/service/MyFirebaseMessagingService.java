package com.example.canteenchecker.canteenmanager.service;

import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static IntentFilter createCanteenChangedIntentFilter() {
        return new IntentFilter(CANTEEN_CHANGED_INTENT_ACTION);
    }

    public static String extractCanteenId(Intent intent) {
        return intent.getStringExtra(CANTEEN_CHANGED_INTENT_CANTEEN_ID);
    }

    private static final String CANTEEN_CHANGED_INTENT_ACTION = "CanteenChanged";
    private static final String CANTEEN_CHANGED_INTENT_CANTEEN_ID = "CanteenId";

    private static final String REMOTE_MESSAGE_TYPE_KEY = "type";
    private static final String REMOTE_MESSAGE_TYPE_VALUE = "canteenDataChanged";
    private static final String REMOTE_MESSAGE_CANTEEN_ID_KEY = "canteenId";

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        if (REMOTE_MESSAGE_TYPE_VALUE.equals(data.get(REMOTE_MESSAGE_TYPE_KEY))) {
            String canteenId = data.get(REMOTE_MESSAGE_CANTEEN_ID_KEY);

            Intent intent = new Intent(CANTEEN_CHANGED_INTENT_ACTION);
            if (canteenId != null) {
                intent.putExtra(CANTEEN_CHANGED_INTENT_CANTEEN_ID, canteenId);
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}
