package com.abishek.comidapartner.splashScreen;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class SCFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList;
    private long baseId = 0;


    public SCFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0: return mFragmentList.get(0); // SCPage1
            case 1: return mFragmentList.get(1); // SCPage2
            case 2: return mFragmentList.get(2); // SCPage3
        }
        return null;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }


}
