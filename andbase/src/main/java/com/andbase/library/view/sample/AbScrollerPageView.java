package com.andbase.library.view.sample;

import android.content.Context;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 页面滚动切换,实现焦点触发和Touch，winphone风格
 */
public class AbScrollerPageView extends ViewGroup {
	
	/** 滚动器. */
	private Scroller scroller;
	
	/** 速度. */
	private VelocityTracker velocityTracker;
	
	/** 当前屏幕状态. */
	private int index = 0;
	
	/** 下一个View的偏移. */
	private int nextViewWidth = 150;
	
	/** 页面切换监听器. */
	private  OnPageChangeListener onPageChangeListener = null;
	
	/** 目标滚动是否完成. */
	private boolean finish = true;

	/**
	 * 构造.
	 * @param context the context
	 */
	public AbScrollerPageView(Context context) {
		super(context);
		scroller = new Scroller(context);

	}

	/**
	 * 构造.
	 * @param context the context
	 * @param attrs the attrs
	 */
	public AbScrollerPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		scroller = new Scroller(context);
	}

	/**
	 * View的位置设定.
	 * @param changed the changed
	 * @param l the l
	 * @param t the t
	 * @param r the r
	 * @param b the b
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //初始布局		
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			int height = child.getMeasuredHeight();
			int width = child.getMeasuredWidth();
			child.setFocusable(true);
			if(i==0){
				child.layout(0,0 ,width , height);
			}else{
				child.layout(width,0,width + nextViewWidth, height);
			}
		}
		
	}

	/**
	 * 测量View的宽高.
	 * @param widthMeasureSpec the width measure spec
	 * @param heightMeasureSpec the height measure spec
	 */
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, height);
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	/**
	 * 打开下一个View.
	 */
	public void showNext() {
        if(!finish ||  index == 1){
			return;
		}
        finish = false;
        index = 1;
		if(onPageChangeListener!=null){
			onPageChangeListener.onPageSelected(1);
		}
		//Log.d(TAG, "--showNext--:"+getScrollX()+" dx "+(getChildAt(1).getWidth()-2*nextViewOffset));
		//始终要以onlayout的位置作为最初始位置计算
		scroller.startScroll(getScrollX(), 0, nextViewWidth, 0, 800);
		invalidate();
	}

	/**
	 * 返回上一个View.
	 */
	public void showPrevious() {
		if(!finish || index == 0){
			return;
		}
        index = 0;
		if(onPageChangeListener!=null){
			onPageChangeListener.onPageSelected(0);
		}
		//Log.d(TAG, "--showPrevious--:"+getScrollX()+" dx -"+getScrollX());
		//滚动回onlayout的0的位置
		scroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 800);
		invalidate();
	}

	/**
	 * 滚动.
	 */
	public void computeScroll() {
		super.computeScroll();
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			postInvalidate();
		}
		//这次滚动结束
		if(scroller.getFinalX()==scroller.getCurrX()){
			finish = true;
		}
	}

	/**
	 * 初始化速度检测.
	 * @param event the event
	 */
	private void obtainVelocityTracker(MotionEvent event) {
		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}
		velocityTracker.addMovement(event);
	}

	/**
	 * 释放速度检测.
	 */
	private void releaseVelocityTracker() {
		if (velocityTracker != null) {
			velocityTracker.recycle();
			velocityTracker = null;
		}
	}

	/**
	 * 获取当前显示的页面位置.
	 * @return the index
	 */
	public int getCurrentIndex() {
		return index;
	}

	/**
	 * 设置主View.
	 * @param view the view
	 */
	public void addContentView(View view) {
		addView(view,0, getLayoutParams());
		view.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					 showPrevious();
				}
				
			}
	    });
		
		view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(MotionEvent.ACTION_DOWN == event.getAction()){
					showPrevious();
				}
				return false;
			}
		});
	}
	
	/**
	 * 设置下一个View.
	 * @param view the view
	 */
	public void addNextView(View view) {
		addView(view,1, getLayoutParams());
        view.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					showNext();
				}
				
			}
	    });
        
        view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(MotionEvent.ACTION_DOWN == event.getAction()){
					showNext();
				}
				return false;
			}
		});
	}

	/**
	 * 设置第二个View的宽度.
	 * @param nextViewWidth the new next view width
	 */
    public void setNextViewWidth(int nextViewWidth) {
        this.nextViewWidth = nextViewWidth;
    }

    /**
     * 设置页面改变监听器.
     * @param onPageChangeListener the new on page change listener
     */
	public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
		this.onPageChangeListener = onPageChangeListener;
	}
	
}
