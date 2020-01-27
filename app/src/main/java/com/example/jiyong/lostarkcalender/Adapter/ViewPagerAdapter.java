package com.example.jiyong.lostarkcalender.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.jiyong.lostarkcalender.TabFragment.MariStuffTabFragment;
import com.example.jiyong.lostarkcalender.TabFragment.RecodeTabFragment;
import com.example.jiyong.lostarkcalender.TabFragment.StatisticsTabFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;

    public ViewPagerAdapter(FragmentManager fm, int NumOfTabs){
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                RecodeTabFragment tab1 = new RecodeTabFragment();
                return tab1;
            case 1:
                StatisticsTabFragment tab2 = new StatisticsTabFragment();
                return tab2;
            case 2:
                MariStuffTabFragment tab3 = new MariStuffTabFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
