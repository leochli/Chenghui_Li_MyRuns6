package com.example.lichenghui.chenghui_li_myruns6;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;


public class HistoryFragment extends Fragment {

    public static ArrayList<MyRunsEntry> MyHistory = new ArrayList<>();
    public static String UPDATE_FROM_CLOUD = "update_from_cloud";
    ListView mHistoryList;
    public static HistoryEntryAdapter mHistoryAdapter;
    private DataSource HisDatasource;

    private BroadcastReceiver historyReiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent i) {
            Log.d(TAG, "onReceive: History Receiver");
            if (HisDatasource!=null){
                MyHistory.clear();
                MyHistory = HisDatasource.ReadEntry();
                mHistoryAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        HisDatasource = new DataSource(getActivity());
        HisDatasource.open();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_history, container, false);

        if (HisDatasource != null) {
            MyHistory = HisDatasource.ReadEntry();
        }
        mHistoryAdapter = new HistoryEntryAdapter(getContext(),
                MyHistory);
        mHistoryList = (ListView) mView.findViewById(R.id.history_list);
        mHistoryList.setAdapter(mHistoryAdapter);

        //Set onClick Listener and launch a display entry activity
        mHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                MyRunsEntry SelectedEntry;
                SelectedEntry = MyHistory.get(position);
                switch (SelectedEntry.InputType){
                    case "GPS":
                    case  "Automatic":
                        Intent DisplayMap = new Intent(getContext(),MapsActivity.class);
                        DisplayMap.putExtra("RequestCode",MapsActivity.MAP_REQUEST_DISPLAY);
                        DisplayMap.putExtra("EntryPosition",position);
                        getContext().startActivity(DisplayMap);
                        break;
                    case "Manual Entry":
                        Intent DisplayIntent = new Intent(getContext(),DisplayEntryActivity.class);
                        DisplayIntent.putExtra("EntryPosition",position);
                        getContext().startActivity(DisplayIntent);
                        break;
                }

            }
        });

        return mView;

    }


    @Override
    public void onResume() {
        HisDatasource.open();
        IntentFilter mFilter = new IntentFilter(UPDATE_FROM_CLOUD);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(historyReiver, mFilter);
        super.onResume();
    }

    @Override
    public void onPause() {
        HisDatasource.close();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(historyReiver);
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}
