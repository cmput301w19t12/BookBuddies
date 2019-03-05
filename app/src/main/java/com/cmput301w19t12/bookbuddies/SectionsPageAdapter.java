package com.cmput301w19t12.bookbuddies;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class SectionsPageAdapter extends FragmentPagerAdapter {
    private final ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    private final ArrayList<String> mFragmentTitles = new ArrayList<String>();

    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment f, String title) {
        mFragmentTitles.add(title);
        mFragmentList.add(f);
    }

    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int pos) {
        return mFragmentTitles.get(pos);
    }
}
