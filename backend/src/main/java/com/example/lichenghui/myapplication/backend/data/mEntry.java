package com.example.lichenghui.myapplication.backend.data;

/**
 * Created by lichenghui on 22/2/2017.
 */

public class mEntry {

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
    public String mInputType;

    //Use String format to store all the data for convenience
    public String mID;
    public String mActivityType;
    public String mDateTime;
    public String mDuration;
    public String mDistance;
    public String mAverageSpeed;
    public String mCalories;
    public String mClimb;
    public String mHeartRate;
    public String mComment;

    public mEntry(String id, String input, String activity, String dateTime, String duration, String distance, String avgSpeed,
                  String calories, String climb, String heartrate, String comment) {

        mID = id;
        mInputType = input;
        mActivityType = activity;
        mDateTime = dateTime;
        mDuration = duration;
        mDistance = distance;
        mAverageSpeed = avgSpeed;
        mCalories = calories;
        mClimb = climb;
        mHeartRate = heartrate;
        mComment = comment;
    }
}
