package com.example.lichenghui.chenghui_li_myruns6;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by lichenghui on 31/1/2017.
 */

public class MyRunsEntry implements Parcelable{
    public static final String EXERCISE_PARENT_ENTITY_ID = "mParent";
    public static final String EXERCISE_PARENT_KEY_ID = "Parent_Key";
    public static final String EXERCISE_ENTITY_ID = "ID";
    public static final String FIELD_NAME_ID = "id";

    public static final String FIELD_NAME_INPUT_TYPE = "input";
    public static final String FIELD_NAME_ACTIVITY_TYPE = "activity";
    public static final String FIELD_NAME_DATETIME = "datetime";
    public static final String FIELD_NAME_DURATION = "duration";
    public static final String FIELD_NAME_DISTANCE = "distance";
    public static final String FIELD_NAME_AVGSPEED = "avgspeed";
    public static final String FIELD_NAME_CALORIES = "calories";
    public static final String FIELD_NAME_CLIMB = "climb";
    public static final String FIELD_NAME_HEARTRATE = "heartrate";
    public static final String FIELD_NAME_COMMENT = "comment";

    private long id;
    public String InputType = "";
    public String ActivityType = "";
    public final static Calendar mCalendar = Calendar.getInstance();
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public String DateAndTime = sdfDate.format(new Date());
    private String mDate = "";
    private String mTime = "";
    public int Duration = 0;
    public double Distance = 0;
    public int Calories = 0;
    public int HeartRate = 0;
    public String Comment = "";
    public double avgspeed= 0,curspeed;
    public double climbsum = 0;

    public LatLng beginLatLng, lastLatLng, newLatLng;
    public double lastheight=-1,newheight;
    public long begintime,curtime;

    //Use points to store path, use google gson to convert arraylist into a inputstring and store
    public ArrayList<LatLng> AllPoints = new ArrayList<>();
    public String PointsString = "";

    public int iscenter = 0;

    public MyRunsEntry(){
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setInputType(String InputType) {
        this.InputType = InputType;
    }
    public void setActivityType(String ActivityType) {
        this.ActivityType = ActivityType;
    }
    public void setDate(Date Date) {
        this.mDate = dateFormatter.format(Date);
        DateAndTime = mDate + mTime;
    }
    public void setTime(Time Time) {
        this.mTime = timeFormatter.format(Time);
        DateAndTime = mDate + mTime;
    }
    public void onDateTimeSet(){
        DateAndTime = sdfDate.format(mCalendar.getTime());
    }
    public void setDateAndTime(String DateAndTime) {this.DateAndTime = DateAndTime;}
    public void setDuration(int Duration) {
        this.Duration = Duration;
    }
    public void setDistance(double Distance) {
        this.Distance = Distance;
    }
    public void setCalories(int Calories) {
        this.Calories = Calories;
    }
    public void setHeartRate(int HeartRate) {
        this.HeartRate = HeartRate;
    }
    public void setComment(String comment) {
        this.Comment = comment;
    }


    //***************Make Entry parcelable**************//

    public MyRunsEntry(Parcel in){
        String[] mStringData = new String[4];
        long[] mLongData = new long[3];
        int[] mIntData = new int[3];
        double[] mDoubleData = new double[6];
        Bundle mPosBundle = new Bundle();

        iscenter = in.readInt();
        in.readStringArray(mStringData);
        in.readLongArray(mLongData);
        in.readIntArray(mIntData);
        in.readDoubleArray(mDoubleData);
        mPosBundle = in.readBundle(ClassLoader.getSystemClassLoader());
        in.readTypedList(AllPoints,LatLng.CREATOR);

        InputType = mStringData[0];
        ActivityType = mStringData[1];
        DateAndTime = mStringData[2];
        Comment = mStringData[3];

        id = mLongData[0];
        begintime = mLongData[1];
        curtime = mLongData[2];

        Duration = mIntData[0];
        Calories = mIntData[1];
        HeartRate = mIntData[2];

        Distance = mDoubleData[0];
        avgspeed = mDoubleData[1];
        curspeed = mDoubleData[2];
        climbsum = mDoubleData[3];
        lastheight = mDoubleData[4];
        newheight = mDoubleData[5];

        beginLatLng = (LatLng)mPosBundle.getParcelable("Begin");
        lastLatLng = (LatLng)mPosBundle.getParcelable("Last");
        newLatLng = (LatLng)mPosBundle.getParcelable("New");


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] mStringData = new String[]{this.InputType,this.ActivityType,this.DateAndTime,Comment
        };
        long[] mLongData = new long[]{id,begintime,curtime};
        int[] mIntData = new int[] {Duration,Calories,HeartRate};
        double[] mDoubleData = new double[]{Distance,avgspeed,curspeed,climbsum,lastheight,
        newheight};
        Bundle mPosBundle = new Bundle();
        mPosBundle.putParcelable("Begin",beginLatLng);
        mPosBundle.putParcelable("Last",lastLatLng);
        mPosBundle.putParcelable("New",newLatLng);

        dest.writeInt(iscenter);
        dest.writeStringArray(mStringData);
        dest.writeLongArray(mLongData);
        dest.writeDoubleArray(mDoubleData);
        dest.writeIntArray(mIntData);
        dest.writeBundle(mPosBundle);
        dest.writeTypedList(AllPoints);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MyRunsEntry createFromParcel(Parcel in) {
            return new MyRunsEntry(in);
        }

        public MyRunsEntry[] newArray(int size) {
            return new MyRunsEntry[size];
        }
    };


}
