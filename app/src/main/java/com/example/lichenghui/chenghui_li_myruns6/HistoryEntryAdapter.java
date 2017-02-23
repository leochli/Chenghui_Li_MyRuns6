package com.example.lichenghui.chenghui_li_myruns6;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lichenghui on 31/1/2017.
 */

public class HistoryEntryAdapter extends ArrayAdapter<MyRunsEntry> {


    public HistoryEntryAdapter(Context context,ArrayList<MyRunsEntry> Myruns) {
        super(context, 0, Myruns);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MyRunsEntry data = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.entry_temp, parent, false);
        }
        // Lookup view for data population
        TextView InputType = (TextView) convertView.findViewById(R.id.input_type);
        TextView ActivityType = (TextView) convertView.findViewById(R.id.activity_type);
        TextView RecordTime = (TextView) convertView.findViewById(R.id.record_time);
        TextView Distance = (TextView) convertView.findViewById(R.id.distance);
        TextView RunningTime = (TextView) convertView.findViewById(R.id.running_time);

        // Populate the data into the template view using the data object
        InputType.setText(data.InputType + ":");
        ActivityType.setText(data.ActivityType + ",");
        RecordTime.setText(data.DateAndTime);
        Distance.setText(String.format("%.02f",data.Distance) + "Miles");
        RunningTime.setText(Integer.toString(data.Duration) + "Minutes");

        // Return the completed view to render on screen
        return convertView;
    }
}
