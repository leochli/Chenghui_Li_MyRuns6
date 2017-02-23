package com.example.lichenghui.chenghui_li_myruns6;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int MAP_REQUEST_TRACK = 0;
    public static final int MAP_REQUEST_DISPLAY = 1;

    public static Gson gson = new Gson();

    private GoogleMap mMap;
    public DataSource mDatasource;
    public MyRunsEntry mEntry;
    private Intent mService;
    private boolean isdisplay = false;


    private Marker currentPos, home;

    private int activity_pos;
    private String input_type;
    private int RequestCode;

    private PolylineOptions rectOptions;
    private Polyline polyline;


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent i) {
            mEntry = i.getParcelableExtra("NewEntry");
            mEntry.InputType = input_type;
            UpdateBoard(mEntry);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.map_tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        mDatasource = new DataSource(this);
        mDatasource.open();

        RequestCode = getIntent().getIntExtra("RequestCode",-1);
        input_type = getIntent().getStringExtra("InputType");

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        MapIsReadyDoNext();
    }

    //After the map is ready, then do next, otherwise error may occur
    private void MapIsReadyDoNext() {
        switch (RequestCode) {
            case MAP_REQUEST_TRACK:
                initParameter();
                mService = new Intent(this, TrackingService.class);
                mService.putExtra("ActivityType", mEntry.ActivityType);
                startService(mService);
                Intent mNotify = new Intent(MapsActivity.this, NotifyService.class);
                MapsActivity.this.startService(mNotify);
                break;
            case MAP_REQUEST_DISPLAY:
                isdisplay = true;
                mEntry = new MyRunsEntry();
                int EntryPos = getIntent().getIntExtra("EntryPosition", -1);
                if (EntryPos != -1)
                    mEntry = HistoryFragment.MyHistory.get(EntryPos);
                else Focus(mEntry.AllPoints.get(0));
                OnlyDisplay(mEntry);
                break;
        }

        //Hide the button if it's in display mode
        if (isdisplay == true){
            Button save = (Button) findViewById(R.id.map_save);
            Button cancel = (Button) findViewById(R.id.map_cancel);
            save.setVisibility(View.INVISIBLE);
            cancel.setVisibility(View.INVISIBLE);
        }
        else {
            Button save = (Button) findViewById(R.id.map_save);
            Button cancel = (Button) findViewById(R.id.map_cancel);
            save.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isdisplay == true) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.map_menu, menu);
            return true;
        } else return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("MyMapData", mEntry);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        mDatasource.open();
        super.onResume();
        IntentFilter mFilter = new IntentFilter(TrackingService.TRACKING_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mFilter);

    }

    @Override
    protected void onPause() {
        mDatasource.close();
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    public void stopServices(){
        if (isdisplay == false) {
            stopService(mService);
            StopNotify();
        }
        else isdisplay = false;
    }
    @Override
    protected void onDestroy(){

        if (isdisplay == false) {
            stopService(mService);
            StopNotify();
        }
        else isdisplay = false;
        super.onDestroy();
    }



    //***********To do***********Save to database
    public void Map_Save(View view) {
        if (home==null) return;
        mEntry.PointsString = gson.toJson(mEntry.AllPoints);

        StopNotify();

        //If the inputtype is automatic, only record the major activity type
        if (mEntry.InputType.equals("Automatic")){
            String mActivity = "";
            int maxvalue = 0;
            for(int i = 0;i<4;i++) {
                if (TrackingService.mAutoActivityCounter[i] >= maxvalue){
                    maxvalue = TrackingService.mAutoActivityCounter[i];
                    mActivity = TrackingService.mActivityType[i];
                }
            }
            mEntry.ActivityType = mActivity;
        }
        stopServices();
        Log.d("TAG", "Map_Save: Activity is " + mEntry.ActivityType);
        mDatasource.CreateEntry(mEntry);
        HistoryFragment.mHistoryAdapter.add(mEntry);
        HistoryFragment.mHistoryAdapter.notifyDataSetChanged();
        finish();

    }


    public void Map_Cancel(View view) {
        StopNotify();
        finish();
    }


//****************Helper function******************//

    //This method is to initialize parameters
    private void initParameter() {

        //Create a new entry
        mEntry = new MyRunsEntry();
        if (input_type.equals("GPS")) {
            activity_pos = getIntent().getIntExtra("Activity", -1);
            if (activity_pos != -1)
                mEntry.ActivityType = (getResources().getStringArray(R.array.activity_type))
                        [activity_pos];
        }
        else {
            mEntry.ActivityType = "Automatic";
        }
    }

    //This method is to move map camera to a certain position
    private void Focus(LatLng mpos) {
        CameraUpdate mCam = CameraUpdateFactory.newLatLngZoom(mpos, 18);
        mMap.animateCamera(mCam);
    }

    private void OnlyDisplay(MyRunsEntry mEntry) {
        //If the origin is not marked yet, mark it as home
        if (home == null) {
            mEntry.beginLatLng = new LatLng(mEntry.AllPoints.get(0).latitude,
                    mEntry.AllPoints.get(0).longitude);
            Focus(mEntry.beginLatLng);
            Log.d("Tag", "DisplayOnly: Markhome");
            home = mMap.addMarker(new MarkerOptions().position(mEntry.beginLatLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            DrawPathbyMarker(home);
        }

        DrawPathbyPoints(mEntry.AllPoints);

        Log.d("Tag", "DisplayOnly: DrawPolylines");
        TextView Type = (TextView) findViewById(R.id.map_type);
        Type.setText("Type: " + mEntry.ActivityType);
        Type.setTextSize(16);
        TextView AvgSpeed = (TextView) findViewById(R.id.map_avg_speed);
        AvgSpeed.setText("AvgSpeed: " + String.format("%.02f", mEntry.avgspeed) + " m/h");
        AvgSpeed.setTextSize(16);
        TextView CurSpeed = (TextView) findViewById(R.id.map_cur_speed);
        CurSpeed.setText("CurSpeed: " + "N/A");
        CurSpeed.setTextSize(16);
        TextView Climb = (TextView) findViewById(R.id.map_climb);
        Climb.setText("Climb: " + String.format("%.02f", mEntry.climbsum * 0.000621371*1000) + " * 10^(-3) Miles");
        Climb.setTextSize(16);
        TextView Calorie = (TextView) findViewById(R.id.map_calorie);
        Calorie.setText("Calorie: " + String.valueOf(mEntry.Calories) + " KCal");
        Calorie.setTextSize(16);
        TextView Distance = (TextView) findViewById(R.id.map_distance);
        Distance.setText("Distance: " + String.format("%.02f", mEntry.Distance*1000) + " * 10^(-3) Miles");
        Distance.setTextSize(16);
    }

    private void DrawPathbyPoints(ArrayList<LatLng> allPoints) {
        for (LatLng mPoints:allPoints
                ) {
            if (mPoints!=allPoints.get(0)) {
                if (currentPos != null)
                    currentPos.remove();
                currentPos = mMap.addMarker(new MarkerOptions().position(mPoints));
                DrawPathbyMarker(currentPos);
            }
        }
    }

    private void DrawPathbyMarker(Marker Newmarker) {
        if (polyline != null) {
            polyline.remove();
            polyline = null;
        }
        if (rectOptions == null)
            rectOptions = new PolylineOptions();
        rectOptions.add(Newmarker.getPosition());
        rectOptions.color(Color.RED);
        polyline = mMap.addPolyline(rectOptions);
    }

    //Update display message by entry
    private void UpdateBoard(MyRunsEntry newEntry) {

        //If the origin is not marked yet, mark it as home
        if (home == null) {
            Focus(mEntry.beginLatLng);
            Log.d("Tag", "onLocationChanged: Markhome");
            home = mMap.addMarker(new MarkerOptions().position(mEntry.beginLatLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            DrawPathbyMarker(home);
        }

        if (currentPos != null)
            currentPos.remove();
        currentPos = mMap.addMarker(new MarkerOptions().position(newEntry.newLatLng));

        DrawPathbyMarker(currentPos);
        TextView Type = (TextView) findViewById(R.id.map_type);
        Type.setText("Type: " + mEntry.ActivityType);
        Type.setTextSize(16);
        TextView AvgSpeed = (TextView) findViewById(R.id.map_avg_speed);
        AvgSpeed.setText("AvgSpeed: " + String.format("%.02f", mEntry.avgspeed) + " m/h");
        AvgSpeed.setTextSize(16);
        TextView CurSpeed = (TextView) findViewById(R.id.map_cur_speed);
        CurSpeed.setText("CurSpeed: " + String.format("%.02f", mEntry.curspeed) + " m/h");
        CurSpeed.setTextSize(16);
        TextView Climb = (TextView) findViewById(R.id.map_climb);
        Climb.setText("Climb: " + String.format("%.02f", mEntry.climbsum * 0.000621371*1000) + " * 10^(-3) Miles");
        Climb.setTextSize(16);
        TextView Calorie = (TextView) findViewById(R.id.map_calorie);
        Calorie.setText("Calorie: " + String.valueOf(mEntry.Calories) + " KCal");
        Calorie.setTextSize(16);
        TextView Distance = (TextView) findViewById(R.id.map_distance);
        Distance.setText("Distance: " + String.format("%.02f", mEntry.Distance*1000) + " * 10^(-3) Miles");
        Distance.setTextSize(16);

    }

    private void StopNotify() {
        Intent intent = new Intent();
        intent.setAction(NotifyService.NOTIFICATION_SERVICE_ACTION);
        sendBroadcast(intent);
    }

    public void Delete_Click(MenuItem item) {
        mDatasource.DeleteEntry(mEntry);
        HistoryFragment.mHistoryAdapter.remove(mEntry);
        HistoryFragment.mHistoryAdapter.notifyDataSetChanged();
        finish();
    }
}