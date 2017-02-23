package com.example.lichenghui.chenghui_li_myruns6;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayEntryActivity extends AppCompatActivity {

    private int EntryPos;
    private MyRunsEntry MyEntry;
    private DataSource mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        mData = new DataSource(this);
        mData.open();

        EntryPos = getIntent().getIntExtra("EntryPosition",-1);
        MyEntry = HistoryFragment.MyHistory.get(EntryPos);

        TextView InputType = (TextView)findViewById(R.id.entry_input_type);
        InputType.setText(MyEntry.InputType);
        TextView ActivityType = (TextView)findViewById(R.id.entry_activity_type);
        ActivityType.setText(MyEntry.ActivityType);
        TextView DateTime = (TextView)findViewById(R.id.entry_date_time);
        DateTime.setText(MyEntry.DateAndTime);
        TextView Duration = (TextView)findViewById(R.id.entry_duration);
        Duration.setText(String.valueOf(MyEntry.Duration) + " Minutes");
        TextView Distance = (TextView)findViewById(R.id.entry_distance);
        Distance.setText(String.valueOf(MyEntry.Distance) + " Miles");
        TextView Calories = (TextView)findViewById(R.id.entry_calories);
        Calories.setText(String.valueOf(MyEntry.Calories) + " Cal");
        TextView HeartRate = (TextView)findViewById(R.id.entry_heart_rate);
        HeartRate.setText(String.valueOf(MyEntry.HeartRate) + " Bpm");
        TextView Comments = (TextView)findViewById(R.id.entry_comments);
        Comments.setText(MyEntry.Comment);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    @Override
    protected void onResume() {
        mData.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mData.close();
        super.onPause();
    }

    public void Delete_Click(MenuItem item) {
        mData.DeleteEntry(MyEntry);
        HistoryFragment.mHistoryAdapter.remove(MyEntry);
        HistoryFragment.mHistoryAdapter.notifyDataSetChanged();
        finish();
    }
}
