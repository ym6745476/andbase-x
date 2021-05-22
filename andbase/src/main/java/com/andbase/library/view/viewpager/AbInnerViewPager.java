package com.andbase.library.view.viewpager;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.andbase.library.view.refresh.AbPullToRefreshView;

import java.util.ArrayList;
import java.util.List;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info scrollView与内部ViewPager滑动的XY冲突
 */
public class AbInnerViewPager extends ViewPager {

	/** 父滚动布局 */
	private List<ViewGroup> parentView;

	int lastX = -1;
	int lastY = -1;

	/**
	 * 构造函数.
	 * @param context the context
	 */
	public AbInnerViewPager(Context context) {
		super(context);
        parentView = new ArrayList<>();
	}

	/**
	 * 构造函数.
	 * @param context the context
	 * @param attrs the attrs
	 */
	public AbInnerViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
        parentView = new ArrayList<>();
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int x = (int) ev.getRawX();
		int y = (int) ev.getRawY();
		int dealtX = 0;
		int dealtY = 0;

		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				dealtX = 0;
				dealtY = 0;
				// 保证子View能够接收到Action_move事件
				getParent().requestDisallowInterceptTouchEvent(true);
				break;
			case MotionEvent.ACTION_MOVE:
				dealtX += Math.abs(x - lastX);
				dealtY += Math.abs(y - lastY);
				if (dealtX >= dealtY) {
                    setParentScrollAble(false);
				} else {
                    setParentScrollAble(true);
				}
				lastX = x;
				lastY = y;
				break;
			case MotionEvent.ACTION_CANCEL:
                setParentScrollAble(true);
				break;
			case MotionEvent.ACTION_UP:
                setParentScrollAble(true);
				break;

		}
		return super.dispatchTouchEvent(ev);
	}


	/**
	 * 设置父级的View.
	 * @param flag 父是否滚动开关
	 */
	private void setParentScrollAble(boolean flag) {
		for(ViewGroup view:parentView){
			view.requestDisallowInterceptTouchEvent(!flag);
		}
	}

	/**
	 * 如果外层有滚动需要设置.
	 * @param parentView the parent view
	 */
	public void addParentView(ViewGroup parentView) {
		this.parentView.add(parentView);
	}

}
