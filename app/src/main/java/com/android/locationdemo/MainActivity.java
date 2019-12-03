package com.android.locationdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.locationdemo.SpeedDetector.LocationModel;
import com.android.locationdemo.SpeedDetector.SpeedDetectorService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

import static com.android.locationdemo.LocationConstants.INTENT_KEY_EVENT_ID;
import static com.android.locationdemo.LocationConstants.INTENT_KEY_LATITUDE;
import static com.android.locationdemo.LocationConstants.INTENT_KEY_LONGITUDE;
import static com.android.locationdemo.LocationConstants.INTENT_KEY_RESULT;
import static com.android.locationdemo.LocationConstants.INTENT_KEY_USER_ID;
import static com.android.locationdemo.LocationConstants.LIVE_LOCATION_BROADCAST_CHANNEL;

public class MainActivity extends AppCompatActivity {

    //    String timer[]={"Select time","1 sec","2 sec","3 sec","4 sec","5 sec"};
    String tim;
    boolean btnStatus = true;
    String result;
    Button btnDetection, btnStopDetection;
    TextView mText,tvStatus;
    LiveLocationSendingService gps;

    //Firebase Work
    DatabaseReference mDatabaseLocationDetails;


    ArrayList<LocationModel> locationModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RunTimePermissions.verifyStoragePermissions(this);

        mText = (TextView) findViewById(R.id.tvCurrentSpeed);
        tvStatus = findViewById(R.id.tvStatus);
        btnDetection = (Button) findViewById(R.id.detection);
        mDatabaseLocationDetails = FirebaseDatabase.getInstance().getReference("currentLocation/").child("user_id_3");

        enable_button();
    }



    private void enable_button() {

        btnDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btnStatus){
                    boolean locationEnabled = LiveLocationSendingService.isLocationEnabled(MainActivity.this);
                    if (locationEnabled) {
                        startService(false);
                    }

                    startService(false);
                    btnStatus = false;
                    btnDetection.setText("Stop");
                }else{
                    stopService();
                    btnStatus = true;
                    btnDetection.setText("Start");
                    mText.setText("");
                }

//                startBroadCastingSampleData();
            }
        });

//        btnStopDetection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                stopService();
//            }
//        });


    }


    public void startService(boolean isReceiving) {
        Intent serviceIntent;
        Intent speedDetectorService;
        if (isReceiving) {
            serviceIntent = new Intent(this, LiveLocationReceivingService.class);
            speedDetectorService = new Intent(this, SpeedDetectorService.class);
            tvStatus.setText("Detection is on");
        } else {

            serviceIntent = new Intent(this, LiveLocationSendingService.class);
            speedDetectorService = new Intent(this, SpeedDetectorService.class);
            tvStatus.setText("Detection is on");

        }

        serviceIntent.putExtra(INTENT_KEY_USER_ID, "1");
        serviceIntent.putExtra(LocationConstants.INTENT_KEY_NOTIFICATION_TITLE, "title");
        serviceIntent.putExtra(LocationConstants.INTENT_KEY_NOTIFICATION_DETAIL, "details");



        ContextCompat.startForegroundService(this, serviceIntent);
        ContextCompat.startForegroundService(this, speedDetectorService);
        Intent intent = new Intent(this, SpeedDetectorService.class);
        Intent speedDetectionIntent = new Intent(this, SpeedDetectorService.class);
        ContextCompat.startForegroundService(this, intent);
        ContextCompat.startForegroundService(this, speedDetectionIntent);
        tvStatus.setText("Detection is on");

    }



    public void stopService() {
        Intent serviceIntent = new Intent(this, LiveLocationSendingService.class);
        Intent speedDetectionServiceIntent = new Intent(this, SpeedDetectorService.class);
        stopService(serviceIntent);
        stopService(speedDetectionServiceIntent);
        tvStatus.setText("Detection is off");
    }


    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(dataReceiver,new IntentFilter("com.locationdemo.showdata"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(dataReceiver);
    }

    @Override
    protected void onDestroy() {
        stopService();
        super.onDestroy();
    }

    BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            mText.setText(intent.getStringExtra(LocationConstants.INTENT_KEY_RESULT));
        }
    };
}
