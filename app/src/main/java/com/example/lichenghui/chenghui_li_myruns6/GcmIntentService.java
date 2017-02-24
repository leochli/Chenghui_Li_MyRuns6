package com.example.lichenghui.chenghui_li_myruns6;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by lichenghui on 22/2/2017.
 */

public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        //Make sure the message is not empty
        if (extras != null && !extras.isEmpty()) {

            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                Log.d(TAG, "onHandleIntent: Device received delete message");
                //get the id and delete the corresponding history
                long rowid = Long.parseLong(extras.getString("message"));

                DataSource mDb = new DataSource(this);
                mDb.open();
                mDb.DelteEntrybyID(rowid);
                //To update the history UI by a localbroadcast
                Intent onCouldUpdated = new Intent(HistoryFragment.UPDATE_FROM_CLOUD);
                LocalBroadcastManager.getInstance(this).sendBroadcast(onCouldUpdated);
                mDb.close();
            }
        }
        //finish the intent with device still awake
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

}