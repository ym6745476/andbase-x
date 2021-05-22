package com.andbase.library.app.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/16 13:27
 * Email 396196516@qq.com
 * Info FragmentPagerAdapter 的适配器
 */
public class AbFragmentPagerAdapter extends FragmentPagerAdapter {


    /**
     * 标题数组.
     */
    private String[] titleList = null;

    /**
     * Fragment列表.
     */
    private List<Fragment> fragmentList = null;


    /**
     * 构造适配器.
     * @param fragmentManager the fragment manager
     * @param fragmentList    the fragment list
     */
    public AbFragmentPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragmentList) {
        super(fragmentManager);
        this.titleList = new String[fragmentList.size()];
        this.fragmentList = fragmentList;
    }

    /**
     * 构造适配器.
     * @param fragmentManager
     * @param titleList
     * @param fragmentList
     */
    public AbFragmentPagerAdapter(FragmentManager fragmentManager, String[] titleList, List<Fragment> fragmentList) {
        super(fragmentManager);
        this.titleList = titleList;
        this.fragmentList = fragmentList;
    }

    /**
     * 获取元素数量.
     * @return the count
     */
    @Override
    public int getCount() {
        return fragmentList.size();
    }

    /**
     * 获取索引位置的Fragment.
     * @param position the position
     */
    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        if (position < fragmentList.size()) {
            fragment = fragmentList.get(position);
        } else {
            fragment = fragmentList.get(0);
        }
        return fragment;

    }

    /**
     * 获取这个位置的标题
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList[position % titleList.length];
    }

}

