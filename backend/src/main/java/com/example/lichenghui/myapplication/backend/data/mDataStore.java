package com.example.lichenghui.myapplication.backend.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.util.ArrayList;

/**
 * Created by lichenghui on 22/2/2017.
 */


public class mDataStore {
    private static final DatastoreService mDatastore = DatastoreServiceFactory
            .getDatastoreService();

    private static Key getKey() {
        return KeyFactory.createKey(mEntry.EXERCISE_PARENT_ENTITY_ID, mEntry.EXERCISE_PARENT_KEY_ID);
    }

    //Refered to gae_sample demo
    public static boolean add(mEntry mData) {
        Key parentKey = getKey();
        Entity entity = new Entity(mEntry.EXERCISE_ENTITY_ID, mData.mID,
                parentKey);

        entity.setProperty(mEntry.FIELD_NAME_ID, mData.mID);
        entity.setProperty(mEntry.FIELD_NAME_INPUT_TYPE, mData.mInputType);
        entity.setProperty(mEntry.FIELD_NAME_ACTIVITY_TYPE, mData.mActivityType);
        entity.setProperty(mEntry.FIELD_NAME_DATETIME, mData.mDateTime);
        entity.setProperty(mEntry.FIELD_NAME_DURATION, mData.mDuration);
        entity.setProperty(mEntry.FIELD_NAME_DISTANCE, mData.mDistance);
        entity.setProperty(mEntry.FIELD_NAME_AVGSPEED, mData.mAverageSpeed);
        entity.setProperty(mEntry.FIELD_NAME_CALORIES, mData.mCalories);
        entity.setProperty(mEntry.FIELD_NAME_CLIMB, mData.mClimb);
        entity.setProperty(mEntry.FIELD_NAME_HEARTRATE, mData.mHeartRate);
        entity.setProperty(mEntry.FIELD_NAME_COMMENT, mData.mComment);

        mDatastore.put(entity);

        return true;
    }

    public static boolean delete(String id) {

        //Delete entry by id
        Query.Filter filter = new Query.FilterPredicate(mEntry.FIELD_NAME_ID,
                Query.FilterOperator.EQUAL, id);
        Query mQuery = new Query(mEntry.EXERCISE_ENTITY_ID);
        mQuery.setFilter(filter);

        PreparedQuery pq = mDatastore.prepare(mQuery);
        Entity result = pq.asSingleEntity();

        boolean ret = false;
        if (result != null) {
            mDatastore.delete(result.getKey());
            ret = true;
        }

        return ret;
    }

    public static ArrayList<mEntry> ReadEntry() {

        ArrayList<mEntry> resultList = new ArrayList<>();

        Query query = new Query(mEntry.EXERCISE_ENTITY_ID);
        query.setFilter(null);
        query.setAncestor(getKey());
        PreparedQuery pq = mDatastore.prepare(query);

        for (Entity entity : pq.asIterable()) {
            mEntry mData;
            if (entity == null)
                mData = null;
            else{
                mData = new mEntry(
                        (String) entity.getProperty(mEntry.FIELD_NAME_ID),
                        (String) entity.getProperty(mEntry.FIELD_NAME_INPUT_TYPE),
                        (String) entity.getProperty(mEntry.FIELD_NAME_ACTIVITY_TYPE),
                        (String) entity.getProperty(mEntry.FIELD_NAME_DATETIME),
                        (String) entity.getProperty(mEntry.FIELD_NAME_DURATION),
                        (String) entity.getProperty(mEntry.FIELD_NAME_DISTANCE),
                        (String) entity.getProperty(mEntry.FIELD_NAME_AVGSPEED),
                        (String) entity.getProperty(mEntry.FIELD_NAME_CALORIES),
                        (String) entity.getProperty(mEntry.FIELD_NAME_CLIMB),
                        (String) entity.getProperty(mEntry.FIELD_NAME_HEARTRATE),
                        (String) entity.getProperty(mEntry.FIELD_NAME_COMMENT));
            }
            if (mData != null) {
                resultList.add(mData);
            }
        }

        return resultList;
    }


}
