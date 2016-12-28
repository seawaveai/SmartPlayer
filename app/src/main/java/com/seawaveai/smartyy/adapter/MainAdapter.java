package com.seawaveai.smartyy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * 创建者     laoyoutiao
 * 创建时间   2016/11/21 20:25
 * 描述	      主界面有2个Fragment
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class MainAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    public MainAdapter(FragmentManager fm, ArrayList<Fragment> fragments) { //传的是fragments集合
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

}
