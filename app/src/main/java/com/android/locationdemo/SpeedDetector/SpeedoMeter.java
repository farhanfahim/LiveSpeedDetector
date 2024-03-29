package com.android.locationdemo.SpeedDetector;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationListener;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.locationdemo.LocationConstants;

import java.util.ArrayList;
import java.util.Random;

public class SpeedoMeter {

    boolean checkPreviousLatLng = false;
    boolean checkInitialSpeed = false;

    double initialSpeed = 0;
    double finalSpeed = 0;

    double timeDifference;

    long startTime,endTime;

    double distance;

    String result;

    /*int i = 1;
    Handler handler = new Handler();
    private ArrayList<LocationModel> locationModels = new ArrayList<>();
    LocationModel locationObject;*/

    OnSpeedChangeListener mSpeedChangeListener;
    LocationModel locationModel = new LocationModel();
    Context context;

    public OnSpeedChangeListener getmSpeedChangeListener() {
        return mSpeedChangeListener;
    }


    public void setmSpeedChangeListener(OnSpeedChangeListener mSpeedChangeListener) {
        this.mSpeedChangeListener = mSpeedChangeListener;
    }


    public SpeedoMeter(Context context) {
        this.context = context;
    }

    public void unregisterLocationReceiver() {
        try{
            LocalBroadcastManager.getInstance(context).unregisterReceiver(locationReceiver);
        }catch (Exception e){
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public interface OnSpeedChangeListener{
        public void onSpeedChange(String result);
    }


    public double distance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return (earthRadius * c);
    }

    public double calcSpeed(long startTime,long endTime){
        double time = endTime-startTime;
        timeDifference = time/1000;
        if(timeDifference==0){
            return 0;
        }
        double millisToSeconds= timeDifference;
        return distance/millisToSeconds;
    }


    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {

            // sample data for testing
            /*locationModels.add( new LocationModel(24.927704,67.095716));
            locationModels.add(new LocationModel(24.928460, 67.096700));
            locationModels.add( new LocationModel(24.929092, 67.097526));
            locationModels.add(new LocationModel(24.929092, 67.097526));
            locationModels.add(new LocationModel(24.9309307,67.0991022));*/


            if(!checkPreviousLatLng){
                locationModel.setpLat(intent.getDoubleExtra(LocationConstants.INTENT_KEY_LATITUDE, 0));
                locationModel.setpLng(intent.getDoubleExtra(LocationConstants.INTENT_KEY_LONGITUDE, 0));
                /*locationObject = locationModels.get(0);
                locationModel.setpLat(locationObject.getLat());
                locationModel.setpLng(locationObject.getLng());*/

                //This method returns the time in millis
                startTime = System.currentTimeMillis();
                checkPreviousLatLng = true;

            }else{
                locationModel.setcLat(intent.getDoubleExtra(LocationConstants.INTENT_KEY_LATITUDE, 0));
                locationModel.setcLng(intent.getDoubleExtra(LocationConstants.INTENT_KEY_LONGITUDE, 0));


                // test with sample data
                   /* handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(i<=4){
                                locationObject = locationModels.get(i);
                                locationModel.setcLat(locationObject.getLat());
                                locationModel.setcLng(locationObject.getLng());*/
                                //This method returns the time in millis
                                endTime = System.currentTimeMillis();

                                distance = distance(locationModel.getpLat(),locationModel.getpLng(),locationModel.getcLat(),locationModel.getcLng());

                                if (!checkInitialSpeed) {
                                    initialSpeed = calcSpeed(startTime, endTime);

                                }
                                if(checkInitialSpeed){
                                    finalSpeed = calcSpeed(startTime, endTime);
                                }


                                double changeInSpeed;
                                if (finalSpeed == 0 && initialSpeed == 0){
                                    changeInSpeed = 0;
                                }else {
                                    if(!checkInitialSpeed){
                                        changeInSpeed = initialSpeed / timeDifference;
                                    }else{
                                        changeInSpeed = finalSpeed - initialSpeed / timeDifference;
                                    }

                                }


                                locationModel.setSpeed(changeInSpeed);

                                //show Full DATA
                                /*result =  ("Initial Speed: "+ initialSpeed
                                        + "\nFinal Speed: "+ finalSpeed
                                        + "\ntime : "+ timeDifference
                                        + "\ndistance : "+ distance
                                        + "\nprevious Lat: "+ locationModel.getpLat()
                                        + "\nprevious long: "+ locationModel.getpLng()
                                        + "\ncurrent Lat: "+ locationModel.getcLat()
                                        + "\ncurrent long: "+ locationModel.getcLng()
                                        + "\nspeed change: "+changeInSpeed);*/

                                // show specific data
                                result =  ("\ntime : "+ timeDifference + "\ndistance : "+ distance  + "\nspeed change: "+changeInSpeed);

                                if(changeInSpeed <= -10 && timeDifference <= 4){
                                    mSpeedChangeListener.onSpeedChange(result+"\nAccident Detected");
                                }else{
                                    mSpeedChangeListener.onSpeedChange(result);
                                }


                                locationModel.setpLat(locationModel.getcLat());
                                locationModel.setpLng(locationModel.getcLng());
                                startTime = endTime;
                                if (checkInitialSpeed){
                                    initialSpeed = finalSpeed;
                                }
                                checkInitialSpeed = true;
                               /* i++;
                            }

                        }
                    }, 2000);*/

                }

            }
    };

    public void registerLocationReceiver() {
        LocalBroadcastManager.getInstance(context).registerReceiver(
                locationReceiver, new IntentFilter(LocationConstants.LIVE_LOCATION_BROADCAST_CHANNEL));
    }

    public BroadcastReceiver getLocationReceiver() {
        return locationReceiver;
    }



}


