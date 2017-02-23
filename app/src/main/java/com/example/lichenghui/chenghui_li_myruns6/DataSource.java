package com.example.lichenghui.chenghui_li_myruns6;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by lichenghui on 31/1/2017.
 */

public class DataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private ArrayList<MyRunsEntry> myRuns;
    private String[] mColumns = {MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_INPUTTYPE, MySQLiteHelper.COLUMN_ACTIVITYTYPE,
            MySQLiteHelper.COLUMN_DATEANDTIME, MySQLiteHelper.COLUMN_DURATION,
            MySQLiteHelper.COLUMN_DISTANCE, MySQLiteHelper.COLUMN_CALORIES,
            MySQLiteHelper.COLUMN_HEARTRATE, MySQLiteHelper.COLUMN_COMMENT,
            MySQLiteHelper.COLUMN_CLIMBSUM, MySQLiteHelper.COLUMN_ALLPOINTS
    };

    public DataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
        myRuns = new ArrayList<>();
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }


    public void close() {
        dbHelper.close();
    }

    public void CreateEntry(MyRunsEntry entry) {
        new CreateEntryTask().execute(entry);
    }

    public void DeleteEntry(MyRunsEntry entry) {
        new DeleteEntryTask().execute(entry);
    }

    public ArrayList<MyRunsEntry> ReadEntry(){

        myRuns.clear();
//        new ReadEntryTask().execute();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_HISTORY,
                mColumns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                myRuns.add(getNewEntry(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return myRuns;

    }

    private MyRunsEntry getNewEntry(Cursor cursor) {
        MyRunsEntry NewEntry = new MyRunsEntry();
        NewEntry.setId(cursor.getLong(0));

        NewEntry.setInputType(cursor.getString(1));
        NewEntry.setActivityType(cursor.getString(2));
        NewEntry.setDateAndTime(cursor.getString(3));
        NewEntry.setDuration(cursor.getInt(4));
        NewEntry.setDistance(cursor.getDouble(5));
        NewEntry.setCalories(cursor.getInt(6));
        NewEntry.setHeartRate(cursor.getInt(7));
        NewEntry.setComment(cursor.getString(8));
        NewEntry.climbsum = cursor.getDouble(9);
        NewEntry.PointsString = cursor.getString(10);
        System.out.println("inputString= " + NewEntry.PointsString);
        Type type = new TypeToken<ArrayList<LatLng>>() {}.getType();
        NewEntry.AllPoints = MapsActivity.gson.fromJson(NewEntry.PointsString, type);
        NewEntry.avgspeed = 60 * NewEntry.Distance / NewEntry.Duration;

        return NewEntry;
    }

    //Async Task to offload main thread
    private class CreateEntryTask extends AsyncTask<MyRunsEntry, Void, Void> {

        @Override
        protected Void doInBackground(MyRunsEntry... mEntry) {
            ContentValues values = new ContentValues();

            values.put(MySQLiteHelper.COLUMN_INPUTTYPE, mEntry[0].InputType);
            values.put(MySQLiteHelper.COLUMN_ACTIVITYTYPE, mEntry[0].ActivityType);
            values.put(MySQLiteHelper.COLUMN_DATEANDTIME, mEntry[0].DateAndTime);
            values.put(MySQLiteHelper.COLUMN_DURATION, mEntry[0].Duration);
            values.put(MySQLiteHelper.COLUMN_DISTANCE, mEntry[0].Distance);
            values.put(MySQLiteHelper.COLUMN_CALORIES, mEntry[0].Calories);
            values.put(MySQLiteHelper.COLUMN_HEARTRATE, mEntry[0].HeartRate);
            values.put(MySQLiteHelper.COLUMN_COMMENT, mEntry[0].Comment);
            values.put(MySQLiteHelper.COLUMN_CLIMBSUM, mEntry[0].climbsum);
            values.put(MySQLiteHelper.COLUMN_ALLPOINTS, mEntry[0].PointsString);

            database.insert(MySQLiteHelper.TABLE_HISTORY, null, values);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... unused) {

        }

        @Override
        protected void onPostExecute(Void unused) {

        }


    }

    public void DelteEntrybyID(long id){
        database.delete(MySQLiteHelper.TABLE_HISTORY, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    private class DeleteEntryTask extends AsyncTask<MyRunsEntry, Void, Void> {

        @Override
        protected Void doInBackground(MyRunsEntry... mEntry) {

            long id = mEntry[0].getId();
            database.delete(MySQLiteHelper.TABLE_HISTORY, MySQLiteHelper.COLUMN_ID
                    + " = " + id, null);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... unused) {

        }

        @Override
        protected void onPostExecute(Void unused) {

        }

    }

    private class ReadEntryTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... unused) {
            Cursor cursor = database.query(MySQLiteHelper.TABLE_HISTORY,
                    mColumns, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    myRuns.add(getNewEntry(cursor));
                    cursor.moveToNext();
                }
            }
            cursor.close();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... unused) {

        }

        @Override
        protected void onPostExecute(Void unused) {

        }

    }
}
