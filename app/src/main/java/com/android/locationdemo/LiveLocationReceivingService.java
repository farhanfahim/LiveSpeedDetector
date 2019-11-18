package com.android.locationdemo;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import static com.android.locationdemo.LocationConstants.CHANNEL_ID;
import static com.android.locationdemo.LocationConstants.DATA_KEY_LASTDATE;
import static com.android.locationdemo.LocationConstants.DATA_KEY_LATITUDE;
import static com.android.locationdemo.LocationConstants.DATA_KEY_LONGITUDE;
import static com.android.locationdemo.LocationConstants.INTENT_KEY_NOTIFICATION_DETAIL;
import static com.android.locationdemo.LocationConstants.INTENT_KEY_NOTIFICATION_TITLE;
import static com.android.locationdemo.LocationConstants.INTENT_KEY_USER_ID;


public class LiveLocationReceivingService extends Service {

    private DatabaseReference mDatabaseLocationDetails;
    private Context mContext;

//    Location location;//Location
    double latitude;//Latitude
    double longitude;//Longitude
    String userId = "", title = "", details = "";

    // The minimum time between updates in milliseconds

    // Declaring a Location Manager
    protected LocationManager mlocationManager;


    public LiveLocationReceivingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.mContext = LiveLocationReceivingService.this;
        userId = intent.getStringExtra(INTENT_KEY_USER_ID);
        title = intent.getStringExtra(INTENT_KEY_NOTIFICATION_TITLE);
        details = intent.getStringExtra(INTENT_KEY_NOTIFICATION_DETAIL);

        mDatabaseLocationDetails = FirebaseDatabase.getInstance().getReference("currentLocation/").child(userId);
        getLocation();


        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(details)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        //do heavy work on a background thread


        //stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    public Location getLocation() {

        mDatabaseLocationDetails.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("LOCATION RECEIVING" , "onChildAdded: " + dataSnapshot.toString());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("LOCATION RECEIVING" , "onChildChanged: " + dataSnapshot.toString());

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d("LOCATION RECEIVING" , "onChildRemoved: " + dataSnapshot.toString());

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("LOCATION RECEIVING" , "onChildMoved: " + dataSnapshot.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("LOCATION RECEIVING" , "onCancelled: " + databaseError.toString());

            }
        });
        return null;
    }


//
//    public double getLatitude() {
//        if (location != null) {
//            latitude = location.getLatitude();
//        }
//
//
//        return latitude;
//    }
//
//    public double getLongitude() {
//        if (location != null) {
//            longitude = location.getLongitude();
//        }
//
//        return longitude;
//    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }







    private void storeInDatabase(double latitude, double longitude) {
        mDatabaseLocationDetails.child(DATA_KEY_LONGITUDE).setValue(longitude);
        mDatabaseLocationDetails.child(DATA_KEY_LATITUDE).setValue(latitude);
        mDatabaseLocationDetails.child(DATA_KEY_LASTDATE).setValue(new Date());
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }



    @Override
    public void onDestroy() {
//        stopUsingGPS();
        super.onDestroy();
    }
}
