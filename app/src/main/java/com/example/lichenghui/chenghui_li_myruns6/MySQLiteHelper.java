package com.example.lichenghui.chenghui_li_myruns6;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lichenghui on 31/1/2017.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_HISTORY = "myrunhistory";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_INPUTTYPE = "input_type";
    public static final String COLUMN_ACTIVITYTYPE = "activity_type";
    public static final String COLUMN_DATEANDTIME = "date_time";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_DISTANCE= "distance";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_HEARTRATE = "heart_rate";
    public static final String COLUMN_COMMENT = "comments";
    public static final String COLUMN_CLIMBSUM = "climbsum";
    public static final String COLUMN_ALLPOINTS = "allpoints";

    private static final String DATABASE_NAME = "history.db";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_HISTORY + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_INPUTTYPE + " text, "
            + COLUMN_ACTIVITYTYPE + " text, "
            + COLUMN_DATEANDTIME +  " text, "
            + COLUMN_DURATION + " INTEGER, "
            + COLUMN_DISTANCE + " FLOAT, "
            + COLUMN_CALORIES + " INTEGER, "
            + COLUMN_HEARTRATE + " INTEGER, "
            + COLUMN_COMMENT + " text, "
            + COLUMN_CLIMBSUM + " FLOAT, "
            + COLUMN_ALLPOINTS + " text);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase mDataBase, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version "
                + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        mDataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(mDataBase);
    }

}
