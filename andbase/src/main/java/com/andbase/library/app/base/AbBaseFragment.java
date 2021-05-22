package com.andbase.library.app.base;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andbase.library.R;
import com.andbase.library.utils.AbLogUtil;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/16 13:27
 * Email 396196516@qq.com
 * Info 所有Fragment要继承这个父类，便于统一管理
 */
public class AbBaseFragment extends Fragment {

    public AbBaseActivity activity;
    public View rootView = null;

    public AbBaseFragment() {
        AbLogUtil.d(this.getClass(),"------------------Fragment构造-----------------------");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AbLogUtil.d(this.getClass(),"------------------onCreateView-----------------------");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        this.activity = (AbBaseActivity)context;
        AbLogUtil.d(this.getClass(),"------------------onAttach-----------------------");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        AbLogUtil.d(this.getClass(),"------------------onDetach-----------------------");
        this.activity = null;
        super.onDetach();
    }

    @Override
    public void onStart() {
        AbLogUtil.d(this.getClass(),"------------------onStart-----------------------");
        super.onStart();
    }

    @Override
    public void onResume() {
        AbLogUtil.d(this.getClass(),"------------------onResume-----------------------");
        super.onResume();
    }

    @Override
    public void onPause() {
        AbLogUtil.d(this.getClass(),"------------------onResume-----------------------");
        super.onPause();
    }

    @Override
    public void onStop() {
        AbLogUtil.d(this.getClass(),"------------------onStop-----------------------");
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        AbLogUtil.d(this.getClass(),"------------------onLowMemory-----------------------");
        super.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        AbLogUtil.d(this.getClass(),"------------------onDestroyView-----------------------");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        AbLogUtil.d(this.getClass(),"------------------onDestroy-----------------------");
        super.onDestroy();
    }

    /**
     * 显示页面
     * @param rootView
     * @param layoutResID  布局ID
     */
    public View showPageView(View rootView,@LayoutRes int layoutResID) {
        View pageView = View.inflate(activity,layoutResID,null);
        ((ViewGroup)rootView).addView(pageView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        return pageView;
    }

    /**
     * 隐藏页面
     * @param rootView
     */
    public void hidePageView(View rootView,View pageView) {
        if(pageView !=null){
            ((ViewGroup)rootView).removeView(pageView);
        }
    }
}
