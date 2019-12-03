package com.android.locationdemo.SpeedDetector;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.locationdemo.MainActivity;
import com.android.locationdemo.R;

import static com.android.locationdemo.LocationConstants.CHANNEL_ID;
import static com.android.locationdemo.LocationConstants.INTENT_KEY_EVENT_ID;
import static com.android.locationdemo.LocationConstants.INTENT_KEY_LATITUDE;
import static com.android.locationdemo.LocationConstants.INTENT_KEY_LONGITUDE;
import static com.android.locationdemo.LocationConstants.INTENT_KEY_RESULT;
import static com.android.locationdemo.LocationConstants.INTENT_KEY_USER_ID;
import static com.android.locationdemo.LocationConstants.LIVE_LOCATION_BROADCAST_CHANNEL;

public class SpeedDetectorService extends Service implements SpeedoMeter.OnSpeedChangeListener {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    SpeedoMeter speedoMeter;

    @Override
    public void onCreate() {
        super.onCreate();
        speedoMeter = new SpeedoMeter(getApplicationContext());
        speedoMeter.setmSpeedChangeListener(this);
        speedoMeter.registerLocationReceiver();
        startInForeground();
    }

    @Override
    public void onDestroy() {
        speedoMeter.unregisterLocationReceiver();
        super.onDestroy();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startInForeground();

        //do heavy work on a background thread


        //stopSelf();

        return START_NOT_STICKY;
    }

    private void startInForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Speed Detector")
                    .setContentText("Speed Detector service started")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, notification);

        }
    }

    @Override
    public void onSpeedChange(String result) {
        //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent("com.locationdemo.showdata");
        // You can also include some extra data.
        intent.putExtra(INTENT_KEY_RESULT, result);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}
