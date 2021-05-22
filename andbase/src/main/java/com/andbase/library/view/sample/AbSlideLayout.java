package com.andbase.library.view.sample;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.andbase.library.R;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/20 13:17
 * Email 396196516@qq.com
 * Info 滑动删除的item
 */
public class AbSlideLayout extends LinearLayout {

    /**分析手势处理的类*/
    private ViewDragHelper viewDragHelper;

    /**第一个view*/
    private View contentView;

    /**第二个view*/
    private View actionView;

    /**距离*/
    private int dragDistance;

    private final double AUTO_OPEN_SPEED_LIMIT = 400.0;

    private int draggedX;

    private Drawable backgroundNormal;

    private Drawable backgroundPressed;

    /**
     * 滑动监听
     */
    private OnSlideListener onSlideListener;

    /**按下的x y*/
    private float downX,downY;

    /**时间*/
    private long lastEventTime;

    /**开关*/
    private boolean slideEnable = true;

    public AbSlideLayout(Context context) {
        this(context, null);
    }

    public AbSlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public AbSlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AbSlideLayout);
        backgroundNormal = typedArray.getDrawable(R.styleable.AbSlideLayout_normalBackground);
        backgroundPressed = typedArray.getDrawable(R.styleable.AbSlideLayout_pressedBackground);
        typedArray.recycle();

        // 创建一个带有回调接口的ViewDragHelper
        viewDragHelper = ViewDragHelper.create(this, new DragHelperCallback());
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        dragDistance = actionView.getMeasuredWidth();
    }

    /**
     * 关闭
     */
    public void slideToStart() {
        if (viewDragHelper != null) {
            viewDragHelper.smoothSlideViewTo(contentView, 0, 0);
            invalidate();
        }
    }

    /**
     * 当View中所有的子控件 均被映射成xml后触发
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 拿到第一个内容显示视图
        contentView = getChildAt(0);
        // 拿到第二个内容显示视图（即删除视图）
        actionView = getChildAt(1);
        // 默认不显示
        actionView.setVisibility(GONE);
    }


    /**
     * 手势处理的监听实现
     */
    private class DragHelperCallback extends ViewDragHelper.Callback {

        // tryCaptureView如何返回ture则表示可以捕获该view，你可以根据传入的第一个view参数决定哪些可以捕获
        @Override
        public boolean tryCaptureView(View view, int i) {
            return view == contentView || view == actionView;
        }

        // 当captureview的位置发生改变时回调
        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            //左边移动了多少
            draggedX = left;

            // 拦截父视图事件，不让父试图事件影响
            getParent().requestDisallowInterceptTouchEvent(true);
            if (changedView == contentView) {
                actionView.offsetLeftAndRight(dx);
            } else {
                contentView.offsetLeftAndRight(dx);
            }
            if (actionView.getVisibility() == View.GONE) {
                actionView.setVisibility(View.VISIBLE);
            }
            //刷新视图
            invalidate();


        }

        /**
         * clampViewPositionHorizontal,
         * clampViewPositionVertical可以在该方法中对child移动的边界进行控制， left , top
         * 分别为即将移动到的位置，比如横向的情况下，我希望只在ViewGroup的内部移动，即：最小>=paddingleft，
         * 最大<=ViewGroup.getWidth()-paddingright-child.getWidth。就可以按照如下代码编写：
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == contentView) {
                final int leftBound = getPaddingLeft();
                final int minLeftBound = -leftBound - dragDistance;
                final int newLeft = Math.min(Math.max(minLeftBound, left), 0);
                return newLeft;
            } else {
                final int minLeftBound = getPaddingLeft()
                        + contentView.getMeasuredWidth() - dragDistance;
                final int maxLeftBound = getPaddingLeft()
                        + contentView.getMeasuredWidth() + getPaddingRight();
                final int newLeft = Math.min(Math.max(left, minLeftBound),
                        maxLeftBound);
                return newLeft;
            }
        }

        /**
         * 如果子View不消耗事件，那么整个手势（DOWN-MOVE*-UP）
         * 都是直接进入onTouchEvent，在onTouchEvent的DOWN的时候就确定了captureView。
         * 如果消耗事件，那么就会先走onInterceptTouchEvent方法，判断是否可以捕获， 而在判断的过程中会去判断另外两个回调的方法：
         * getViewHorizontalDragRange和getViewVerticalDragRange，
         * 只有这两个方法返回大于0的值才能正常的捕获。所以， 如果你用Button测试，或者给TextView添加了clickable = true
         * ，都要记得重写下面这两个方法：
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return dragDistance;
        }

        // 手指释放的时候回调
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            boolean settleToOpen = false;
            if (xvel > AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = false;
            } else if (xvel < -AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = true;
            } else if (draggedX <= -dragDistance / 2) {
                settleToOpen = true;
            } else if (draggedX > -dragDistance / 2) {
                settleToOpen = false;
            }

            final int settleDestX = settleToOpen ? -dragDistance : 0;
            if (onSlideListener != null) {
                if (settleDestX == 0) {
                    contentView.setBackgroundDrawable(backgroundNormal);
                    onSlideListener.onSlided(false);
                } else {
                    onSlideListener.onSlided(true);
                }
            }
            viewDragHelper.smoothSlideViewTo(contentView, settleDestX, 0);
            ViewCompat.postInvalidateOnAnimation(AbSlideLayout.this);
        }
    }

    public void setOnSlideListener(OnSlideListener onSlideListener) {
        this.onSlideListener = onSlideListener;
    }



    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(slideEnable){
            if (viewDragHelper.shouldInterceptTouchEvent(event)) {
                return true;
            }
            return super.onInterceptTouchEvent(event);
        }else{
            return true;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //记录按下的坐标
            downX = event.getRawX();
            downY = event.getRawY();
            //显示点击触感
            lastEventTime = event.getEventTime();

            contentView.setBackgroundDrawable(backgroundPressed);

        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            float downNewX = event.getRawX();
            float downNewY = event.getRawY();
            //x，y移动的距离小于10就出发点击事件
            if (Math.abs(downX - downNewX) < 10  && Math.abs(downY - downNewY) < 10) {
                if (onSlideListener != null) {
                    onSlideListener.onClick();
                }
            }

            downX = 0;
            downY = 0;

            contentView.setBackgroundDrawable(backgroundNormal);

        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            float downNewX = event.getRawX();
            float downNewY = event.getRawY();
            if(Math.abs(downNewX - downX) < Math.abs(downNewY - downY) + 10){
                return false;
            }

            if(Math.abs(downNewX - downX) > 5 ||  Math.abs(downNewY - downY) > 5){
                contentView.setBackgroundDrawable(backgroundNormal);
            }

            if(isLongPressed(downNewX,downNewY,event.getEventTime())){

            }
        }

        if(slideEnable){
            viewDragHelper.processTouchEvent(event);
        }
        return true;

    }

    public boolean isLongPressed(float thisX,float thisY, long thisEventTime) {
        float offsetX = Math.abs(thisX - downX);
        float offsetY = Math.abs(thisY - downY);
        long intervalTime = thisEventTime - lastEventTime;
        if (offsetX <= 10 && offsetY <= 10 && intervalTime >= 10) {
            return true;
        }
        return false;
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * 由于整个视图都用了ViewDragHelper手势处理，
     * 所以导致不滑动的视图点击事件不可用，所以需要自己处理点击事件
     */
    public interface OnSlideListener {
        /**
         * 侧滑完了之后调用 true已经侧滑，false还未侧滑
         */
        void onSlided(boolean isSlide);

        /**
         * 未侧滑状态下的默认显示整体的点击事件
         */
        void onClick();
    }

    public boolean isSlideEnable() {
        return slideEnable;
    }

    public void setSlideEnable(boolean slideEnable) {
        this.slideEnable = slideEnable;
    }
}

