package com.abishek.comidapartner.Home.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.abishek.comidapartner.Home.fragment.History;
import com.abishek.comidapartner.Home.fragment.InProgress;
import com.abishek.comidapartner.Home.fragment.NewOrder;


public class TabAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    ViewPager viewPager;
    public TabAdapter(Context c, FragmentManager fm, int totalTabs, ViewPager viewPager) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
        this.viewPager = viewPager;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new NewOrder();
            case 1:
                return new InProgress();
            case 2:
                return new History();
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}