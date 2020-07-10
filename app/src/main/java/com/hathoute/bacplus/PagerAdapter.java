package com.hathoute.bacplus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int noOfTabs;
    private int Year;
    private int Option;
    private int Subject;

    public PagerAdapter(FragmentManager fm, int noOfTabs, int Year, int Option, int Subject) {
        super(fm);
        this.noOfTabs = noOfTabs;
        this.Year = Year;
        this.Option = Option;
        this.Subject = Subject;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment tabFragment;
        switch(position) {
            case 0:
                tabFragment = new LessonsFragment();
                break;
            case 1:
                tabFragment = new ExamsFragment();
                break;
            case 2:
                tabFragment = new VideosFragment();
                break;
            default:
                tabFragment = new ExamsFragment();
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("year", Year);
        bundle.putInt("option", Option);
        bundle.putInt("subject", Subject);
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
