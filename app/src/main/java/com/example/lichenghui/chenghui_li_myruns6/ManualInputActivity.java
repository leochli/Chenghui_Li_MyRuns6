package com.example.lichenghui.chenghui_li_myruns6;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import com.google.gson.Gson;

public class ManualInputActivity extends AppCompatActivity {

    private ListView MyManualList;
    public MyRunsEntry mEntry;
    public DataSource mDatasource;
    private int activity_pos;
    public static final int Date = 0;
    public static final int Time = 1;
    public static final int Duration = 2;
    public static final int Distance = 3;
    public static final int Calories = 4;
    public static final int HeartRate = 5;
    public static final int Comment = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_input);
        setTitle("Manual Entry");

        mDatasource = new DataSource(this);
        mDatasource.open();
        mEntry = new MyRunsEntry();
        mEntry.InputType = "Manual Entry";
        activity_pos = getIntent().getIntExtra("Activity",-1);
        if (activity_pos != -1)
            mEntry.ActivityType = (getResources().getStringArray(R.array.activity_type))
                    [activity_pos];

        final String[] ListItems = getResources().getStringArray(R.array.manual_input_list);
        MyManualList = (ListView) findViewById(android.R.id.list);
        ArrayAdapter<String> ListAdapter = new ArrayAdapter<String>(this,
                R.layout.mylist_layout, ListItems);
        MyManualList.setAdapter(ListAdapter);

        //Set onClickListener
        MyManualList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                Calendar mCalendar = Calendar.getInstance();
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ManualInputActivity.this);
                final EditText mText;

                switch (position) {
                    //Set Datepicker
                    case Date:
                        DatePickerDialog mDatePicker = new DatePickerDialog(ManualInputActivity.this,
                                new DatePickerDialog.OnDateSetListener(){
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int day)
                                    {
                                        ManualInputActivity.this.onDateSet(year,month,day);
                                    }
                                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                                mCalendar.get(Calendar.DAY_OF_MONTH) );
                        mDatePicker.show();
                        break;

                    //Set Timepicker
                    case Time:
                        TimePickerDialog mTimePicker = new TimePickerDialog(ManualInputActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hour, int minute)
                                    {
                                        ManualInputActivity.this.onTimeSet(hour,minute);
                                    }
                                }, mCalendar.HOUR, mCalendar.MINUTE,false);
                        mTimePicker.show();
                        break;

                    //Build up Diaglog
                    case Duration:
                    case Distance:
                    case Calories:
                    case HeartRate:

                        mText = new EditText(getBaseContext());
                        mText.setText("");
                        //To give unit hints
                        switch (position)  {
                            case Duration:
                                mText.setHint("Minutes");
                                mText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                break;
                            case Distance:
                                mText.setHint("Miles");
                                mText.setInputType(InputType.TYPE_CLASS_NUMBER
                                        | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                break;
                            case Calories:
                                mText.setHint("Cal");
                                mText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                break;
                            case HeartRate:
                                mText.setHint("Bpm");
                                mText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                break;
                        }

                        //Set textview to alertdialog builder
                        mBuilder.setView(mText);

                        //Set dialog message
                        mBuilder.setTitle(ListItems[position])
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int btnid)
                                    {
                                        if (!mText.getText().toString().isEmpty()) {
                                            switch (position) {
                                                case Duration:
                                                    onDurationSet(mText.getText().toString());
                                                    break;
                                                case Distance:
                                                    onDistanceSet(mText.getText().toString());
                                                    break;
                                                case Calories:
                                                    onCaloriesSet(mText.getText().toString());
                                                    break;
                                                case HeartRate:
                                                    onHeartrateSet(mText.getText().toString());
                                                    break;
                                            }
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int btnid)
                                    {}
                                });

                        //Create Dialog and show it
                        AlertDialog NumberDialog = mBuilder.create();
                        NumberDialog.show();
                        break;

                    case Comment:
                        //get view
                        mText = new EditText(getBaseContext());
                        mText.setHint("Please leave your comment here");
                        mText.setInputType(InputType.TYPE_CLASS_TEXT);

                        //Set textview to alertdialog builder
                        mBuilder.setView(mText);

                        //Set dialog message
                        mBuilder.setTitle(ListItems[position])
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int btnid)
                                    {
                                        if (!mText.getText().toString().isEmpty())
                                            onCommentSet(mText.getText().toString());
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int btnid)
                                    {}
                                });

                        //Create Dialog and show it
                        AlertDialog CommentDialog = mBuilder.create();
                        CommentDialog.show();
                        break;


                }
            }
        });
    }

    @Override
    protected void onResume() {
        mDatasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mDatasource.close();
        super.onPause();
    }



    //***************  Helper Functions *********************************


    public void onDateSet(int year, int monthOfYear, int dayOfMonth) {
        MyRunsEntry.mCalendar.set(year,monthOfYear,dayOfMonth);
//        java.util.Date mDate = new Date(year,monthOfYear,dayOfMonth);
//        mEntry.setDate(mCalendar.getD);
        mEntry.onDateTimeSet();
    }

    public void onTimeSet(int hourOfDay, int minute) {
        MyRunsEntry.mCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        MyRunsEntry.mCalendar.set(Calendar.MINUTE,minute);
        mEntry.onDateTimeSet();
    }

    public void onDurationSet(String strDurationInMinutes) {
        if (strDurationInMinutes != "")
            mEntry.setDuration(Integer.valueOf(strDurationInMinutes));
    }

    public void onDistanceSet(String strDistance) {
        if (strDistance != "")
            mEntry.setDistance(Double.valueOf(strDistance));
    }

    public void onCaloriesSet(String strCalories) {
        if (strCalories != "")
            mEntry.setCalories(Integer.valueOf(strCalories));
    }

    public void onHeartrateSet(String strHeartrate) {
        if (strHeartrate != "")
            mEntry.setHeartRate(Integer.valueOf(strHeartrate));
    }

    public void onCommentSet(String comment) {
        mEntry.setComment(comment);
    }


    public void Save_Click(View view){
        Gson gson = new Gson();
        mEntry.PointsString= gson.toJson(mEntry.AllPoints);
        System.out.println("inputString= " + mEntry.PointsString);
        mDatasource.CreateEntry(mEntry);
        HistoryFragment.mHistoryAdapter.add(mEntry);
        HistoryFragment.mHistoryAdapter.notifyDataSetChanged();
        finish();
    }

    //Go back to previous activity
    public void Cancle_Click(View view) {
        finish();
    }
}
