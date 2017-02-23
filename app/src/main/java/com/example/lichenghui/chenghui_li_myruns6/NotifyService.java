package com.example.lichenghui.chenghui_li_myruns6;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by lichenghui on 9/2/2017.
 */

public class NotifyService extends Service {

    final static String NOTIFICATION_SERVICE_ACTION = "MyNotifyService";

    //Receive the bradcast message from map activity and stop notification
    public BroadcastReceiver mNotifyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        IntentFilter mFilter = new IntentFilter(NOTIFICATION_SERVICE_ACTION);
        registerReceiver(mNotifyReceiver, mFilter);

        // Send Notification
        Intent MapIntent = new Intent(this, MapsActivity.class);
        MapIntent.putExtra("RequestCode",MapsActivity.MAP_REQUEST_TRACK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),
                0, MapIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

        Notification mNote = new Notification.Builder(this)
                .setContentTitle("MyRuns5!")
                .setContentText("Your running app is tracking you").setSmallIcon(R.drawable.dartmouth)
                .setContentIntent(pendingIntent).build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNote.flags = mNote.flags | Notification.FLAG_ONGOING_EVENT;


        notificationManager.notify(0, mNote);
        Log.d("TAG", "onStartCommand: Start Notification");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        this.unregisterReceiver(mNotifyReceiver);
        super.onDestroy();
    }


}
