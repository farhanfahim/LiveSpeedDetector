package com.android.locationdemo;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.android.locationdemo.LocationConstants.INTENT_KEY_LATITUDE;
import static com.android.locationdemo.LocationConstants.INTENT_KEY_LONGITUDE;
import static com.android.locationdemo.LocationConstants.INTENT_KEY_USER_ID;
import static com.android.locationdemo.LocationConstants.LIVE_LOCATION_BROADCAST_CHANNEL;

public class ServiceMessageHandler {

    private static void sendDataToActivity(Context context, double lat, double lng, String userId) {
        Intent intent = new Intent(LIVE_LOCATION_BROADCAST_CHANNEL);
        // You can also include some extra data.
        intent.putExtra(INTENT_KEY_LATITUDE, lat);
        intent.putExtra(INTENT_KEY_LONGITUDE, lng);
        intent.putExtra(INTENT_KEY_USER_ID, userId);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


}
