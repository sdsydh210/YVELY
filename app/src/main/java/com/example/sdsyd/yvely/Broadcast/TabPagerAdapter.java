package com.example.sdsyd.yvely.Broadcast;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.sdsyd.yvely.UserInfoFragment;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    //Count number of tabs
    private int tabCount;

    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        //Returning the current tabs
        switch (position){
            case 0:
                ListLiveBroadcastingFragment listLiveBroadcastingFragment = new ListLiveBroadcastingFragment();
                return listLiveBroadcastingFragment;
            case 1:
                ListVodFragment listVodFragment = new ListVodFragment();
                return listVodFragment;
            case 2:
                UserInfoFragment userInfoFragment = new UserInfoFragment();
                return userInfoFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
