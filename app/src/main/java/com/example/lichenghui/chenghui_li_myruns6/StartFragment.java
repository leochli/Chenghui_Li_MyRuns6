package com.example.lichenghui.chenghui_li_myruns6;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.lichenghui.chenghui_li_myruns6.MyRunsEntry.FIELD_NAME_ACTIVITY_TYPE;
import static com.example.lichenghui.chenghui_li_myruns6.MyRunsEntry.FIELD_NAME_AVGSPEED;
import static com.example.lichenghui.chenghui_li_myruns6.MyRunsEntry.FIELD_NAME_CALORIES;
import static com.example.lichenghui.chenghui_li_myruns6.MyRunsEntry.FIELD_NAME_CLIMB;
import static com.example.lichenghui.chenghui_li_myruns6.MyRunsEntry.FIELD_NAME_COMMENT;
import static com.example.lichenghui.chenghui_li_myruns6.MyRunsEntry.FIELD_NAME_DATETIME;
import static com.example.lichenghui.chenghui_li_myruns6.MyRunsEntry.FIELD_NAME_DISTANCE;
import static com.example.lichenghui.chenghui_li_myruns6.MyRunsEntry.FIELD_NAME_DURATION;
import static com.example.lichenghui.chenghui_li_myruns6.MyRunsEntry.FIELD_NAME_HEARTRATE;
import static com.example.lichenghui.chenghui_li_myruns6.MyRunsEntry.FIELD_NAME_ID;
import static com.example.lichenghui.chenghui_li_myruns6.MyRunsEntry.FIELD_NAME_INPUT_TYPE;
import static com.google.android.gms.plus.PlusOneDummyView.TAG;


public class StartFragment extends Fragment implements View.OnClickListener{


    private Spinner InputSpinner;
    private Spinner ActivitySpinner;
    private ArrayAdapter<CharSequence> InputAdapter;
    private ArrayAdapter<CharSequence> ActivityAdapter;
    private Button StartButton;
    private Button SyncButton;
    private int position;
    private int activity_pos;
    private static final int manual_activity_pos = 0;
    private static final int map_activity_pos = 1;
    private static final int automatic_pos = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_start, container, false);

        InputSpinner = (Spinner) myView.findViewById(R.id.input_spinner);
        ActivitySpinner = (Spinner) myView.findViewById(R.id.activity_spinner);

        InputAdapter = ArrayAdapter.createFromResource(myView.getContext(),R.array.input_type,
                android.R.layout.simple_spinner_item);
        ActivityAdapter = ArrayAdapter.createFromResource(myView.getContext(),R.array.activity_type,
                android.R.layout.simple_spinner_item);

        InputAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        InputSpinner.setAdapter(InputAdapter);

        ActivityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ActivitySpinner.setAdapter(ActivityAdapter);

        InputSpinner.setOnItemSelectedListener(new InputTypeSpinner());

        //set on click listener
        StartButton = (Button) myView.findViewById(R.id.start_btn);
        StartButton.setOnClickListener(this);
        SyncButton = (Button) myView.findViewById(R.id.sync_btn);
        SyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Sync");
                new SyncTask().execute();
            }
        });
        return myView;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle inState){
        super.onActivityCreated(inState);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_btn: {
                position = InputSpinner.getSelectedItemPosition();
                switch (position){

                    case manual_activity_pos:
                        Intent MaunalActivityIntent = new Intent(getContext(),ManualInputActivity.class);
                        activity_pos = ActivitySpinner.getSelectedItemPosition();
                        MaunalActivityIntent.putExtra("Activity",activity_pos);
                        getContext().startActivity(MaunalActivityIntent);
                        break;

                    case map_activity_pos:
                        Intent MapActivity = new Intent(getContext(),MapsActivity.class);
                        activity_pos = ActivitySpinner.getSelectedItemPosition();
                        MapActivity.putExtra("InputType","GPS");
                        MapActivity.putExtra("Activity",activity_pos);
                        MapActivity.putExtra("RequestCode",MapsActivity.MAP_REQUEST_TRACK);
                        getContext().startActivity(MapActivity);
                        break;

                    case automatic_pos:
                        Intent autoActivity = new Intent(getContext(),MapsActivity.class);
                        activity_pos = ActivitySpinner.getSelectedItemPosition();
                        autoActivity.putExtra("InputType","Automatic");
                        autoActivity.putExtra("Activity",activity_pos);
                        autoActivity.putExtra("RequestCode",MapsActivity.MAP_REQUEST_TRACK);
                        getContext().startActivity(autoActivity);

                        break;
                }

            }
            case R.id.sync_btn: {
//                new SyncTask().execute();
                break;
            }
        }
    }

    //Refer to gcm demo
    private class SyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            DataSource mDataSource = new DataSource(getContext());
            mDataSource.open();
            try{
                ArrayList<MyRunsEntry> mEntries = mDataSource.ReadEntry();
                mDataSource.close();

                if(mEntries == null)
                    return null;

                //Store all the data into a JSON Array
                JSONArray uploadArray = new JSONArray();

                for(MyRunsEntry entry: mEntries){
                    //Create a JSON object to store the data
                    JSONObject mObject = new JSONObject();
                    mObject.put(FIELD_NAME_ID, Long.toString(entry.getId()));
                    mObject.put(FIELD_NAME_INPUT_TYPE, entry.InputType);
                    mObject.put(FIELD_NAME_ACTIVITY_TYPE,entry.ActivityType);
                    mObject.put(FIELD_NAME_DATETIME, entry.DateAndTime);
                    mObject.put(FIELD_NAME_DURATION, Integer.toString(entry.Duration));
                    mObject.put(FIELD_NAME_DISTANCE,String.format("%.2f",entry.Distance)+"Miles");
                    mObject.put(FIELD_NAME_AVGSPEED,String.format("%.2f",entry.avgspeed)+"Miles");
                    mObject.put(FIELD_NAME_CALORIES,Integer.toString(entry.Calories));
                    mObject.put(FIELD_NAME_CLIMB,Double.toString(entry.climbsum)+"Miles");
                    mObject.put(FIELD_NAME_HEARTRATE, Integer.toString(entry.HeartRate));
                    mObject.put(FIELD_NAME_COMMENT, entry.Comment);
                    // Add to json array
                    uploadArray.put(mObject);
                }

                Map<String,String> params = new HashMap<>();
                params.put("result",uploadArray.toString());
                params.put("regId", MainActivity.GCMid);

                ServerUtilities.post(MainActivity.SERVER_ADDR+"/PostData.do", params);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG, "doInBackground: SyncTask Done!");
            return null;
        }
    }
    
}
