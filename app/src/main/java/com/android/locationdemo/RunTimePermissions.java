package com.android.locationdemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

/**
 * Created by khanhamza on 23-May-17.
 */

public class RunTimePermissions {



    // Storage Permissions variables
    private static final int REQUEST_CODE = 6361;
    private static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };



    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int locationPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarsePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        int internetPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.INTERNET);
        int wifiPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_WIFI_STATE);
        int readPhonePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);

        if (locationPermission != PackageManager.PERMISSION_GRANTED
                || coarsePermission != PackageManager.PERMISSION_GRANTED
                ) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS,
                    REQUEST_CODE
            );
        }
    }
}
