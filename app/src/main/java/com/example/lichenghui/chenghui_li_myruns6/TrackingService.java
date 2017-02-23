package com.example.lichenghui.chenghui_li_myruns6;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ArrayBlockingQueue;

import static android.content.ContentValues.TAG;


/**
 * Created by lichenghui on 9/2/2017.
 */

public class TrackingService extends Service implements LocationListener, SensorEventListener {

    private final IBinder mBinder = new TrackingBinder();
    public static final String TRACKING_ACTION = "com.example.lichenghui.chenghui_li_myruns4.TACKING_ACTION";

    public final static String[] mActivityType = new String[]{"Standing","Walking","Running","Others"};
    //Used to calculate the major activity in auto-mode
    public static int[] mAutoActivityCounter = new int[4];

    public MyRunsEntry mEntry;
    LocationManager mLocationManager;
    private SensorManager mSensorManager;
    private ArrayBlockingQueue<Double> mAccBuffer;
    private SensorTask mSensorTask;

    public boolean isRunning = false;
    private ArrayList<Double> mFeatVec;

    @Override
    public void onCreate() {
        super.onCreate();

        mAccBuffer = new ArrayBlockingQueue<Double>(2048);
        mFeatVec = new ArrayList<Double>();

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("TAG", "onBind()");
        return (mBinder);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        initParameter();
        isRunning = true;
        mEntry.ActivityType = intent.getStringExtra("ActivityType");
        if (mEntry.ActivityType.equals("Automatic"))
        {
            //Register sensor listener on start of the  service
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(
                    Sensor.TYPE_LINEAR_ACCELERATION),SensorManager.SENSOR_DELAY_FASTEST);
            Log.d(TAG, "onStartCommand: SensorListener Registered");


            //Create an asynctask that keep dealing with new sensor data
//            SensorTask mSensorTask = new SensorTask();
//            mSensorTask.execute();

            //It's weired that a new thread works but I cannot run a AysncTask for a second time
            Thread SensorThread = new Thread() {
                @Override
                public void run() {
                    try {
                        Log.d("TAGG","Async");
                        double[] dataBlock = new double[64];
                        double[] real = dataBlock;
                        double[] im = new double[64];
                        int i = 0;
                        double max = 0;
                        //Set the activity counter to 0
                        for (int k = 0; k < 4;k++)
                            mAutoActivityCounter[k] = 0;
                        while (true){
                            if (!isRunning)
                                break;
                            try {
                                dataBlock[i] = mAccBuffer.take();
                                i++;
                                //If the data array reaches size 64, generate a feature vector
                                if (i >= 64){
                                    i = 0;
                                    max = 0;
                                    for (double mAcc : dataBlock){
                                        if (max < mAcc)
                                            max = mAcc;
                                    }

                                    FFT mFFT = new FFT(64);
                                    mFFT.fft(real,im);
                                    for (int j =0; j<real.length;j++){
                                        double mag = Math.sqrt(real[j]*real[j] + im[j]*im[j]);
                                        mFeatVec.add(mag);
                                        im[j] = 0;
                                    }

                                    //finally add the max value to the feature vector
                                    mFeatVec.add(max);
                                    double type = WekaClassifier.classify(mFeatVec.toArray());
                                    mFeatVec.clear();
                                    mEntry.setActivityType(mActivityType[(int)type]);
                                    mAutoActivityCounter[(int)type]++;
                                    Log.d(TAG, "Acitivity type updated "+mEntry.ActivityType);
                                }
                            }  catch (Exception e) {
                                e.printStackTrace();
                            }

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            SensorThread.start();

        }

        StartLocationUpdate();
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            double mag = Math.sqrt(event.values[0]*event.values[0]+event.values[1]*event.values[1]+
                    event.values[2]*event.values[2]);

            try {
                mAccBuffer.add(mag);
            }catch (IllegalStateException e){
                //Enlarge the buffer if it is full
                ArrayBlockingQueue mNewBuffer = new ArrayBlockingQueue(mAccBuffer.size()*2);

                mAccBuffer.drainTo(mNewBuffer);
                mAccBuffer = mNewBuffer;
                mAccBuffer.add(mag);
            }

        }
    }


    @Override
    public void onLocationChanged(Location location) {
        if (mEntry.iscenter == 0) {
            //Set the activity counter to 0
            for (int k = 0; k < 4;k++)
                mAutoActivityCounter[k] = 0;

            Markhome(location);
            mEntry.iscenter = 1;
        }
        else {
            UpdateEntryLocation(location);
            Intent onEntryUpdated = new Intent(TRACKING_ACTION);
            onEntryUpdated.putExtra("NewEntry", mEntry);
            LocalBroadcastManager.getInstance(this).sendBroadcast(onEntryUpdated);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class TrackingBinder extends Binder {
        TrackingService getService() {
            // Return instance so clients can call public methods
            return TrackingService.this;
        }
    }

    private void initParameter() {

        //Create a new entry
        mEntry = new MyRunsEntry();
        mEntry.InputType = "GPS";
        mEntry.AllPoints = new ArrayList<LatLng>();

    }

    private void StartLocationUpdate() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = mLocationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = mLocationManager.getLastKnownLocation(provider);
        onLocationChanged(location);
        mLocationManager.requestLocationUpdates(provider, 500, 0, this);
    }

    private void checkPermission() {

    }


    //Initialize the entry
    private void Markhome(Location location) {
        Double mlat,mlng;
        mlat = location.getLatitude();
        mlng = location.getLongitude();
        mEntry.begintime = Calendar.getInstance().getTimeInMillis();
        mEntry.beginLatLng = new LatLng(mlat,mlng);

        //Record the begin point
        mEntry.AllPoints.add(mEntry.beginLatLng);
        Log.d(TAG, "Markhome: ");
    }

    //Update the entry
    public void UpdateEntryLocation(Location location){

        Double mlat,mlng;
        mlat = location.getLatitude();
        mlng = location.getLongitude();
        mEntry.newLatLng = new LatLng(mlat,mlng);
        mEntry.newheight = location.getAltitude();

        //Record the path by point
        mEntry.AllPoints.add(mEntry.newLatLng);

        //Calculate the total distance in meters;
        if (mEntry.lastLatLng!=null) {
            float[] results = new float[1];
            Location.distanceBetween(
                    mEntry.lastLatLng.latitude, mEntry.lastLatLng.longitude,
                    mEntry.newLatLng.latitude, mEntry.newLatLng.longitude,results);
            mEntry.Distance = mEntry.Distance + results[0]*0.000621371;
            mEntry.Calories = (int)(mEntry.Distance * 505);
        }
        mEntry.lastLatLng = mEntry.newLatLng;

        //Calculate the climb in meters
        if (mEntry.lastheight!=-1 && mEntry.newheight>mEntry.lastheight){
            mEntry.climbsum = mEntry.climbsum + mEntry.newheight - mEntry.lastheight;
        }
        mEntry.lastheight = mEntry.newheight;

        //Get the speed
        if (location.hasSpeed()) {
            mEntry.curspeed = location.getSpeed();
        }
        mEntry.curtime = Calendar.getInstance().getTimeInMillis();
        mEntry.Duration = (int)((mEntry.curtime - mEntry.begintime)/60000)+1;
        mEntry.avgspeed = 60 * mEntry.Distance / mEntry.Duration;

    }




    @Override
    public void onDestroy(){
        super.onDestroy();
        checkPermission();
        isRunning = false;
        if(mLocationManager != null)
            mLocationManager.removeUpdates(this);

        if (mSensorManager !=null)
            mSensorManager.unregisterListener(this);
        stopSelf();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private class SensorTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {

            Log.d("TAGG","Async");
            double[] dataBlock = new double[64];
            double[] real = dataBlock;
            double[] im = new double[64];
            int i = 0;
            double max = 0;
            //Set the activity counter to 0
            for (int k = 0; k < 4;k++)
                mAutoActivityCounter[k] = 0;
            while (true){
                if (isCancelled())
                    return null;
                try {
                    dataBlock[i] = mAccBuffer.take();
                    i++;
                    //If the data array reaches size 64, generate a feature vector
                    if (i >= 64){
                        i = 0;
                        max = 0;
                        for (double mAcc : dataBlock){
                            if (max < mAcc)
                                max = mAcc;
                        }

                        FFT mFFT = new FFT(64);
                        mFFT.fft(real,im);
                        for (int j =0; j<real.length;j++){
                            double mag = Math.sqrt(real[j]*real[j] + im[j]*im[j]);
                            mFeatVec.add(mag);
                            im[j] = 0;
                        }

                        //finally add the max value to the feature vector
                        mFeatVec.add(max);
                        double type = WekaClassifier.classify(mFeatVec.toArray());
                        mFeatVec.clear();
                        mEntry.setActivityType(mActivityType[(int)type]);
                        mAutoActivityCounter[(int)type]++;
                        Log.d(TAG, "doInBackground: Acitivity type updated "+mEntry.ActivityType);
                    }
                }  catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
