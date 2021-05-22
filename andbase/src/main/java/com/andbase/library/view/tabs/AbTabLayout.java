package com.andbase.library.view.tabs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andbase.library.R;
import com.andbase.library.utils.AbViewUtil;
import com.andbase.library.view.listener.AbOnPositionChangedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2018/5/17 17:54
 * Email 396196516@qq.com
 * Info TabLayout.
 */
public class AbTabLayout extends LinearLayout {

    /** The context. */
    private Context context;

    /** The m tab selector. */
    private Runnable tabSelector;

    /** The m max tab width. */
    public int maxTabWidth;

    /** tab的背景. */
    private int tabBackgroundResource = -1;

    /** tab的文字大小. */
    private int tabTextSize = 15;

    /** tab的文字左右边距. */
    private int tabTextPaddingLR = 20;

    /** tab的文字颜色. */
    private int tabTextColor = Color.BLACK;

    /** 是否粗体. */
    private boolean tabTextBold = false;

    /** tab的选中文字颜色. */
    private int tabSelectedColor = Color.BLACK;

    /** tab的线性布局. */
    private LinearLayout tabLinearLayout = null;

    /** tab的线性布局父. */
    private HorizontalScrollView tabScrollView  = null;

    /** tab的文字. */
    private List<String> tabItemTextList = null;

    /** tab的图标. */
    private List<Drawable> tabItemDrawableList = null;

    /** tab的列表. */
    private ArrayList<TextView> tabItemList = null;

    /** 当前页卡编号. */
    private int selectedIndex;

    private AbOnPositionChangedListener positionChangedListener = null;

    public AbTabLayout(Context context) {
        this(context, null);
    }


    public AbTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    public void initView(Context context){
        this.context = context;
        this.setOrientation(LinearLayout.VERTICAL);

        tabScrollView  = new HorizontalScrollView(context);
        tabScrollView.setHorizontalScrollBarEnabled(false);
        tabScrollView.setSmoothScrollingEnabled(true);

        tabLinearLayout = new LinearLayout(context);
        tabLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        tabLinearLayout.setGravity(Gravity.CENTER);

        tabTextSize = (int) AbViewUtil.sp2px(context,tabTextSize);
        tabTextPaddingLR = (int) AbViewUtil.dip2px(context,tabTextPaddingLR);

        tabScrollView.addView(tabLinearLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        this.addView(tabScrollView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //定义Tab栏
        tabItemTextList = new ArrayList<String>();
        tabItemList = new ArrayList<TextView>();
        tabItemDrawableList = new ArrayList<Drawable>();

    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
        tabScrollView.setFillViewport(lockedExpanded);

        final int childCount = tabLinearLayout.getChildCount();
        if (childCount > 1 && (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)) {
            if (childCount > 2) {
                maxTabWidth = (int)(MeasureSpec.getSize(widthMeasureSpec) * 0.4f);
            } else {
                maxTabWidth = MeasureSpec.getSize(widthMeasureSpec) / 2;
            }
        } else {
            maxTabWidth = -1;
        }

        final int oldWidth = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int newWidth = getMeasuredWidth();

        if (lockedExpanded && oldWidth != newWidth) {
            setCurrentItem(selectedIndex);
        }
    }

    /**
     * Animate to tab.
     * @param position the position
     */
    private void animateToTab(final int position) {
        final View tabView = tabLinearLayout.getChildAt(position);
        if (tabSelector != null) {
            removeCallbacks(tabSelector);
        }
        tabSelector = new Runnable() {
            public void run() {
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                tabScrollView.smoothScrollTo(scrollPos, 0);
                tabSelector = null;
            }
        };
        post(tabSelector);
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (tabSelector != null) {
            post(tabSelector);
        }
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (tabSelector != null) {
            removeCallbacks(tabSelector);
        }
    }

    /**
     * 创造一个Tab.
     * @param text the text
     * @param index the index
     */
    private void addTab(String text, int index) {
        addTab(text,index,null);
    }

    /**
     * 创造一个Tab.
     * @param text the text
     * @param index the index
     * @param top the top
     */
    private void addTab(String text,final int index,Drawable top) {

        AbTabItemView tabView = new AbTabItemView(this.context);

        if(tabBackgroundResource!=-1){
            tabView.setTabBackgroundResource(tabBackgroundResource);
        }
        if(top!=null){
            tabView.setTabCompoundDrawables(null, top, null, null);
        }
        tabView.setTabTextColor(tabTextColor);

        tabView.setTabTextSize(tabTextSize);

        if(tabTextBold){
            tabView.setTabTextBold();
        }
        tabView.init(index,text,tabTextPaddingLR);
        tabItemList.add(tabView.getTextView());
        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentItem(index);
            }
        });
        LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT,1);
        lp.leftMargin = 10;
        lp.rightMargin = 10;
        tabLinearLayout.addView(tabView, lp);
    }

    /**
     * tab有变化刷新.
     */
    public void notifyTabDataSetChanged() {
        tabLinearLayout.removeAllViews();
        tabItemList.clear();
        final int count = tabItemTextList.size();
        for (int i = 0; i < count; i++) {
            if(tabItemDrawableList.size()>0){
                addTab(tabItemTextList.get(i), i,tabItemDrawableList.get(i));
            }else{
                addTab(tabItemTextList.get(i), i);
            }

        }
        if (selectedIndex > count) {
            selectedIndex = count - 1;
        }
        setCurrentItem(selectedIndex);
        requestLayout();
    }

    /**
     * 设置显示哪一个.
     * @param item the new current item
     */
    public void setCurrentItem(int item) {
        selectedIndex = item;
        final int tabCount = tabLinearLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final AbTabItemView child = (AbTabItemView)tabLinearLayout.getChildAt(i);
            final boolean isSelected = (i == item);
            child.setSelected(isSelected);
            if (isSelected) {
                child.setTabTextColor(tabSelectedColor);
                animateToTab(item);
            }else{
                child.setTabTextColor(tabTextColor);
            }
        }
        if(positionChangedListener!=null){
            positionChangedListener.onPositionChanged(item);
        }
    }


    /**
     * 设置tab文字的颜色.
     * @param tabColor the tab text color
     */
    public void setTabTextColor(int tabColor) {
        this.tabTextColor = tabColor;
    }

    /**
     * 设置tab文字的粗体
     */
    public void setTabTextBold() {
        this.tabTextBold = true;
    }

    /**
     * 设置选中的颜色.
     * @param tabColor the tab select color
     */
    public void setTabSelectedColor(int tabColor) {
        this.tabSelectedColor = tabColor;
    }

    public void setTabTextPaddingLR(int tabTextPaddingLR) {
        this.tabTextPaddingLR = (int) AbViewUtil.dip2px(context,tabTextPaddingLR);
    }

    /**
     * 设置文字大小 单位sp.
     * @param textSize the tab text size
     */
    public void setTabTextSize(int textSize) {
        this.tabTextSize = textSize;
    }

    /**
     * 设置文字大小 单位px
     * 如果要兼容超大屏，不规则屏幕都用px做单位
     * @param textSizePx the tab text size
     */
    public void setTabTextSizePx(int textSizePx) {
        this.tabTextSize = (int) AbViewUtil.scaleValue(context,textSizePx);
    }

    /**
     * 设置单个tab的背景选择器.
     * @param resid the tab background resource
     */
    public void setTabBackgroundResource(int resid) {
        tabBackgroundResource = resid;
    }

    /**
     * 设置Tab的背景.
     * @param resid the tab layout background resource
     */
    public void setTabLayoutBackgroundResource(int resid) {
        this.tabLinearLayout.setBackgroundResource(resid);
    }

    /**
     * 增加一组内容与tab.
     * @param textList the tab texts
     */
    public void addTabs(List<String> textList){
        tabItemTextList.addAll(textList);
        notifyTabDataSetChanged();
    }

    /**
     * 增加一组内容与tab.
     * @param textList the tab texts
     * @param drawables the drawables
     */
    public void addTabs(List<String> textList,List<Drawable> drawables){
        tabItemTextList.addAll(textList);
        tabItemDrawableList.addAll(drawables);
        notifyTabDataSetChanged();
    }

    /**
     * 增加一个内容与tab.
     * @param text the tab text
     */
    public void addTab(String text){
        tabItemTextList.add(text);
        notifyTabDataSetChanged();
    }

    /**
     * 增加一个内容与tab.
     * @param text the tab text
     * @param drawable the drawable
     */
    public void addTab(String text,Drawable drawable){
        tabItemTextList.add(text);
        tabItemDrawableList.add(drawable);
        notifyTabDataSetChanged();
    }


    /**
     * 删除某一个.
     * @param index the index
     */
    public void removeTab(int index){
        tabLinearLayout.removeViewAt(index);
        tabItemList.remove(index);
        tabItemDrawableList.remove(index);
        tabItemTextList.remove(index);
        notifyTabDataSetChanged();
    }

    /**
     * 删除所有.
     */
    public void removeAllTabs(){
        tabLinearLayout.removeAllViews();
        tabItemList.clear();
        tabItemDrawableList.clear();
        tabItemTextList.clear();
        notifyTabDataSetChanged();
    }

    /**
     * 设置每个tab的边距.
     * @param left the left
     * @param top the top
     * @param right the right
     * @param bottom the bottom
     */
    public void setTabPadding(int left, int top, int right, int bottom) {
        for(int i = 0;i<tabItemList.size();i++){
            TextView tv = tabItemList.get(i);
            tv.setPadding(left, top, right, bottom);
        }
    }

    public AbOnPositionChangedListener getPositionChangedListener() {
        return positionChangedListener;
    }

    public void setPositionChangedListener(AbOnPositionChangedListener positionChangedListener) {
        this.positionChangedListener = positionChangedListener;
    }

    public class AbTabItemView extends RelativeLayout {

        /** The m context. */
        private Context context;

        /** 当前的索引. */
        private int index;

        /** 包含的TextView. */
        private TextView textView;

        /** 滑动条. */
        private TextView lineView = null;

        /** 图片. */
        private Drawable leftDrawable,topDrawable,rightDrawable,bottomDrawable;

        /** Bounds. */
        private int leftBounds,topBounds,rightBounds,bottomBounds;

        public AbTabItemView(Context context) {
            this(context,null);
        }


        public AbTabItemView(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.context = context;

            textView = new TextView(context);
            textView.setId(R.id.ab_text_view);
            textView.setGravity(Gravity.CENTER);

            textView.setFocusable(true);
            textView.setCompoundDrawablePadding(10);
            textView.setSingleLine();

            LayoutParams lp =  new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
            this.addView(textView,lp);

            LayoutParams lp2 =  new LayoutParams(LayoutParams.MATCH_PARENT,5);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
            lp2.addRule(RelativeLayout.ALIGN_LEFT,R.id.ab_text_view);
            lp2.addRule(RelativeLayout.ALIGN_RIGHT,R.id.ab_text_view);
            lineView = new TextView(context);
            this.addView(lineView,lp2);

            //this.setBackgroundColor(context.getResources().getColor(R.color.red));

        }

        public void init(int index,String text,int paddingLR) {
            this.index = index;
            textView.setText(text);
            textView.setPadding(paddingLR,0,paddingLR,0);
        }

        public int getIndex() {
            return index;
        }

        public TextView getTextView() {
            return textView;
        }

        /**
         * 设置文字大小.
         * @param tabTextSize the tab text size
         */
        public void setTabTextSize(int tabTextSize) {
            textView.setTextSize(tabTextSize);
        }

        /**
         * 设置文字颜色.
         * @param tabColor the tab text color
         */
        public void setTabTextColor(int tabColor) {
            textView.setTextColor(tabColor);
        }

        /**
         * 设置文字粗体.
         */
        public void setTabTextBold() {
            TextPaint tp = textView.getPaint();
            tp.setFakeBoldText(true);
        }

        /**
         * 设置选中.
         */
        public void setSelected(boolean selected) {
            if(lineView == null){
                return;
            }
            if(selected){
                lineView.setBackgroundColor(tabSelectedColor);
            }else{
                lineView.setBackgroundResource(R.color.transparent);
            }
        }

        /**
         * 设置文字的图片.
         * @param left the left
         * @param top the top
         * @param right the right
         * @param bottom the bottom
         */
        public void setTabCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
            leftDrawable = left;
            topDrawable = top;
            rightDrawable = right;
            bottomDrawable = bottom;

            if(leftDrawable!=null){
                leftDrawable.setBounds(leftBounds, topBounds, rightBounds, bottomBounds);
            }
            if(topDrawable!=null){
                topDrawable.setBounds(leftBounds, topBounds, rightBounds, bottomBounds);
            }
            if(rightDrawable!=null){
                rightDrawable.setBounds(leftBounds, topBounds, rightBounds, bottomBounds);
            }
            if(bottomDrawable!=null){
                bottomDrawable.setBounds(leftBounds, topBounds, rightBounds, bottomBounds);
            }
            textView.setCompoundDrawables(leftDrawable, topDrawable, rightDrawable, bottomDrawable);
        }

        /**
         * 设置图片尺寸.
         * @param left the left
         * @param top the top
         * @param right the right
         * @param bottom the bottom
         */
        public void setTabCompoundDrawablesBounds(int left, int top, int right, int bottom) {
            leftBounds = AbViewUtil.scaleValue(context, left);
            topBounds = AbViewUtil.scaleValue(context, top);
            rightBounds = AbViewUtil.scaleValue(context, right);
            bottomBounds = AbViewUtil.scaleValue(context, bottom);
        }

        /**
         * 设置tab的背景选择.
         * @param resid the tab background resource
         */
        public void setTabBackgroundResource(int resid) {
            this.setBackgroundResource(resid);
        }

        /**
         * 设置tab的背景选择.
         *
         * @param drawable the tab background drawable
         */
        public void setTabBackgroundDrawable(Drawable drawable) {
            this.setBackgroundDrawable(drawable);
        }

    }
}
