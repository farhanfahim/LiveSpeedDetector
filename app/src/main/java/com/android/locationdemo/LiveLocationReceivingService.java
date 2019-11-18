package com.android.locationdemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.android.locationdemo.LocationConstants.CHANNEL_ID;
import static com.android.locationdemo.LocationConstants.DATA_KEY_LATITUDE;
import static com.android.locationdemo.LocationConstants.DATA_KEY_LONGITUDE;
import static com.android.locationdemo.LocationConstants.INTENT_KEY_NOTIFICATION_DETAIL;
import static com.android.locationdemo.LocationConstants.INTENT_KEY_NOTIFICATION_TITLE;
import static com.android.locationdemo.LocationConstants.INTENT_KEY_USER_ID;


public class LiveLocationReceivingService extends Service {

    private DatabaseReference mDatabaseLocationDetails;
    private Context mContext;
    String userId = "", title = "", details = "";


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


    public void getLocation() {

        mDatabaseLocationDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("LOCATION RECEIVING", "onDataChange: " + dataSnapshot.toString());

                try {
                    double latitude = (double) dataSnapshot.child(DATA_KEY_LATITUDE).getValue();
                    double longitude = (double) dataSnapshot.child(DATA_KEY_LONGITUDE).getValue();
                    ServiceMessageHandler.sendDataToActivity(mContext, latitude, longitude, userId, LocationConstants.EVENT_FROM_FIREBASE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("LOCATION RECEIVING", "onCancelled: " + databaseError.toString());
            }
        });
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
        mDatabaseLocationDetails.addValueEventListener(null);
        super.onDestroy();
    }
}
