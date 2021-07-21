package com.andbase.library.view.viewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.andbase.library.R;
import com.andbase.library.app.adapter.AbFragmentPagerAdapter;
import com.andbase.library.app.adapter.AbViewPagerAdapter;
import com.andbase.library.app.model.AbGalleryTransformer;
import com.andbase.library.utils.AbViewUtil;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import java.util.ArrayList;
import java.util.List;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 带指示器的ViewPager
 */
public class AbIndicatorViewPager extends LinearLayout {

	/** 上下文. */
	private Context context;

	/** 内部的ViewPager. */
	private AbInnerViewPager viewPager;

	/** 指示器. */
	private AbIndicatorView indicatorView;

	/** List views. */
	private List<View> viewList = null;

	/** List views. */
	private List<Fragment> fragmentList = null;

	/** 适配器. */
	private AbViewPagerAdapter viewPagerAdapter = null;
	private AbFragmentPagerAdapter fragmentPagerAdapter = null;

	/** 子View不越界. */
	private boolean clipChildren = true;
	private boolean arcBottom = true;

	/** 是否滚动中. */
	private boolean playEnabled = false;

	/**
	 * 创建一个AbIndicatorViewPager.
	 * @param context the context
	 */
	public AbIndicatorViewPager(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * 从xml初始化的AbIndicatorViewPager.
	 * @param context the context
	 * @param attrs the attrs
	 */
	public AbIndicatorViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AbIndicatorViewPager);
		clipChildren = typedArray.getBoolean(R.styleable.AbIndicatorViewPager_clip_children, true);
		arcBottom = typedArray.getBoolean(R.styleable.AbIndicatorViewPager_arc_bottom, false);
		typedArray.recycle();
		initView(context);
	}
	
	/**
	 * 初始化这个View.
	 * @param context the context
	 */
	public void initView(Context context){
		this.context = context;
		this.setOrientation(LinearLayout.VERTICAL);
		View view = null;
		if(!clipChildren){
			view = View.inflate(context, R.layout.ab_indicator_view_pager,null);
		}else{
			view = View.inflate(context, R.layout.ab_indicator_view_pager_clip,null);
		}

		//view.setBackgroundResource(R.color.green);
		this.addView(view,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));


		viewPager = (AbInnerViewPager)view.findViewById(R.id.indicator_view_pager);
		indicatorView = (AbIndicatorView)this.findViewById(R.id.indicator_view);

		viewPager.setOffscreenPageLimit(3);
        if(!clipChildren){
            viewPager.setPageMargin(0);
        }else{
            viewPager.setPageMargin(20);
			viewPager.setPageTransformer(false, new AbGalleryTransformer());
        }
		if(arcBottom){
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)indicatorView.getLayoutParams();
			lp.setMargins(0,0,0, (int)AbViewUtil.dip2px(context,10));
			indicatorView.setLayoutParams(lp);
		}


		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				indicatorView.setSelect(position);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

	}

	public void setViewAdapter(List<View> viewList){
		this.viewList = viewList;
		this.viewPagerAdapter = new AbViewPagerAdapter(context, viewList);
		this.viewPager.setAdapter(viewPagerAdapter);
	}

	public void setFragmentAdapter(FragmentManager fragmentManager,List<Fragment> fragmentList){
		this.fragmentList = fragmentList;
		this.fragmentPagerAdapter = new AbFragmentPagerAdapter(fragmentManager, fragmentList);
		this.viewPager.setAdapter(fragmentPagerAdapter);
	}

	/**
	 * 更新
	 */
	public void notifyDataSetChanged(){
		if(viewPagerAdapter!=null){
			indicatorView.setCount(viewList.size());
			viewPagerAdapter.notifyDataSetChanged();
		}else if(fragmentPagerAdapter!=null){
			indicatorView.setCount(fragmentList.size());
			fragmentPagerAdapter.notifyDataSetChanged();
		}

	}

	/**
	 * 获取这个滑动的ViewPager类.
	 *
	 * @return
	 */
	public AbInnerViewPager getViewPager() {
		return viewPager;
	}
	
	/**
	 * 获取当前的View的数量.
	 *
	 * @return
	 */
	public int getCount() {
		if(viewPagerAdapter!=null){
			return viewList.size();
		}else if(fragmentPagerAdapter!=null){
			return fragmentList.size();
		}else{
			return 0;
		}
	}

	/**
	 * 添加View.
	 * @param views the views
	 */
	public void addItemViews(List<View> views){
		viewList.addAll(views);
		notifyDataSetChanged();
	}

	/**
	 * 添加View.
	 * @param view the view
	 */
	public void addItemView(View view){
		viewList.add(view);
		notifyDataSetChanged();
	}

	/**
	 * 添加View.
	 * @param index the index
	 * @param view the view
	 */
	public void addItemView(int index,View view){
		viewList.add(index,view);
	}

	/**
	 * 添加View.
	 * @param view the view
	 */
	public void addItemView(View view,boolean notify){
		viewList.add(view);
		if(notify){
			notifyDataSetChanged();
		}

	}

	/**
	 * 删除View
	 */
	public void removeItemView(View view){
		if(viewPagerAdapter!=null){
			viewList.remove(view);
			notifyDataSetChanged();
		}
	}

	/**
	 * 删除View.
	 */
	public void removeAllItemViews(){
		if(viewPagerAdapter!=null){
			viewList.clear();
			notifyDataSetChanged();
		}else if(fragmentPagerAdapter!=null){
			fragmentList.clear();
			notifyDataSetChanged();
		}

	}

	public AbIndicatorView getIndicatorView() {
		return indicatorView;
	}

	/** 用与轮换的 handler. */
	private Handler handler = new Handler();

	/** 用于轮播的线程. */
	private Runnable runnable = new Runnable() {
		public void run() {
			try{
				int count = viewList.size();
				int i = viewPager.getCurrentItem();
				if(count == 0){
					viewPager.setCurrentItem(0, true);
				}else{
					viewPager.setCurrentItem((i+1)%count, true);
				}

				handler.postDelayed(runnable, 10000);
			}catch(Exception e){
				e.printStackTrace();
			}

		}
	};


	/**
	 * 自动轮播.
	 */
	public void startPlay(){
		handler.removeCallbacks(runnable);
		handler.postDelayed(runnable, 10000);
	}

	/**
	 * 自动轮播.
	 */
	public void stopPlay(){
		handler.removeCallbacks(runnable);
	}


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
				if(playEnabled){
					stopPlay();
				}
                break;
            case MotionEvent.ACTION_UP:
            	if(playEnabled){
					startPlay();
				}
                break;
            case MotionEvent.ACTION_CANCEL:
				if(playEnabled) {
					startPlay();
				}
                break;
        }
        return viewPager.dispatchTouchEvent(ev);
    }

	public boolean isPlayEnabled() {
		return playEnabled;
	}

	public void setPlayEnabled(boolean playEnabled) {
		this.playEnabled = playEnabled;
	}

    public List<View> getViewList() {
        return viewList;
    }

    public void setViewList(List<View> viewList) {
        this.viewList = viewList;
    }

    public List<Fragment> getFragmentList() {
        return fragmentList;
    }

    public void setFragmentList(ArrayList<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
    }
}
