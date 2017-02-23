package com.example.lichenghui.chenghui_li_myruns6;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by lichenghui on 22/2/2017.
 */

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //To handle the intent
        Log.d(TAG, "onReceive: GcmReceiver");
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        //Wake the service and keep device awake
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}