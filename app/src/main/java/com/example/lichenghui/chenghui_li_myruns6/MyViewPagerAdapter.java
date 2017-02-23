package com.example.lichenghui.chenghui_li_myruns6;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by lichenghui on 14/1/2017.
 */

public class MyViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> MyFragments;

    public MyViewPagerAdapter(FragmentManager fragment_manager, ArrayList<Fragment>
                              fragments) {
        super(fragment_manager);
        MyFragments = fragments;
    }

    @Override
    public int getCount(){
        return MyFragments.size();
    }

    @Override
    public Fragment getItem(int position){
        return MyFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position){
        if (position == 0 )
            return "Start";
        else if (position ==1 )
            return "History";
        else if (position ==2 )
            return "Settings";

        return null;
    }

}
