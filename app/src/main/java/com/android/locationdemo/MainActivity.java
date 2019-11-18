package com.android.locationdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    //    String timer[]={"Select time","1 sec","2 sec","3 sec","4 sec","5 sec"};
    String tim;
    Button mLocationBtn, stopService;
    TextView mText;
    LiveLocationSendingService gps;

    //Firebase Work
    DatabaseReference mDatabaseLocationDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RunTimePermissions.verifyStoragePermissions(this);

        mText = (TextView) findViewById(R.id.location_tv);
        mLocationBtn = (Button) findViewById(R.id.location_btn);
        stopService = (Button) findViewById(R.id.stopService);
        mDatabaseLocationDetails = FirebaseDatabase.getInstance().getReference("currentLocation/").child("user_id_3");

        enable_button();
    }

    private void enable_button() {

        mLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                boolean locationEnabled = LiveLocationSendingService.isLocationEnabled(MainActivity.this);
//                if (locationEnabled) {
//                    startService(false);
//                }

                startService(true);
            }
        });

        stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService();
            }
        });


    }


    public void startService(boolean isReceiving) {
        Intent serviceIntent;
        if (isReceiving) {
            serviceIntent = new Intent(this, LiveLocationReceivingService.class);
        } else {
            serviceIntent = new Intent(this, LiveLocationSendingService.class);

        }


        serviceIntent.putExtra(LocationConstants.INTENT_KEY_USER_ID, "1");
        serviceIntent.putExtra(LocationConstants.INTENT_KEY_NOTIFICATION_TITLE, "title");
        serviceIntent.putExtra(LocationConstants.INTENT_KEY_NOTIFICATION_DETAIL, "details");

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, LiveLocationSendingService.class);
        stopService(serviceIntent);
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String userId = intent.getStringExtra(LocationConstants.INTENT_KEY_USER_ID);
            double lat = intent.getDoubleExtra(LocationConstants.INTENT_KEY_LATITUDE, 0);
            double lng = intent.getDoubleExtra(LocationConstants.INTENT_KEY_LONGITUDE, 0);
            int event = intent.getIntExtra(LocationConstants.INTENT_KEY_EVENT_ID, -1);


            mText.setText("Lat: " + lat + " -- Long: " + lng + "  -- userid: " + userId);


        }
    };

    public void registerBroadcastService() {
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(
                mMessageReceiver, new IntentFilter(LocationConstants.LIVE_LOCATION_BROADCAST_CHANNEL));
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcastService();
    }

    @Override
    protected void onDestroy() {
        stopService();
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}
