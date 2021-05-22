package com.andbase.library.view.tabs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andbase.library.R;
import com.andbase.library.app.adapter.AbFragmentPagerAdapter;
import com.andbase.library.utils.AbColorUtil;
import com.andbase.library.utils.AbLogUtil;
import com.andbase.library.view.viewpager.AbViewPager;

import java.util.ArrayList;
import java.util.List;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 滑动的tab,tab不固定超出后可滑动.
 * 用TabLayout+ViewPager实现
 */
public class AbTopNavView extends LinearLayout {

	/** The context. */
	private Context context;

    /** TabLayout. */
    private TabLayout tabLayout;

	/** ViewPager. */
	private AbViewPager viewPager;

    /** FragmentManager. */
    private FragmentManager fragmentManager;

	/** 内容区域的适配器. */
    private AbFragmentPagerAdapter fragmentPagerAdapter = null;

    /** tab的文字. */
    private String[] tabTextArray = null;

    /** tab的图标. */
    private List<Drawable[]> tabDrawableList = null;

    /** tab的文字颜色. */
    private int[] tabTextColors = null;

    /** tab的文字颜色. */
    private int tabIndicatorColor;

    /** tab的文字大小sp. */
    private int tabTextSize = 14;

    /** tab的文字View. */
    private List<TextView> tabTextViewList = null;

    /** 内容的View. */
    private List<Fragment> tabFragmentList = null;

    /**
     * 构造函数..
     * @param context the context
     */
    public AbTopNavView(Context context) {
        this(context, null);
    }

    /**
     * 构造函数.
     * @param context the context
     * @param attrs the attrs
     */
    public AbTopNavView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        //要求必须是FragmentActivity的实例
        if(!(this.context instanceof FragmentActivity)){
            AbLogUtil.e(AbTopNavView.class, "构造AbTabPagerView的参数context,必须是FragmentActivity的实例。");
            return;
        }

        this.fragmentManager =  ((FragmentActivity)this.context).getSupportFragmentManager();
        initView();
    }

    /**
     * 如果控件的父亲是个Fragment，就不要使用xml声明，而是应该用这个方法代替，
     * 关键是getChildFragmentManager，否则你的fragment将不能显示内容
     * @param fragment the fragment
     */
    public AbTopNavView(Fragment fragment) {
        super(fragment.getActivity());
        this.context = fragment.getActivity();
        this.fragmentManager = fragment.getChildFragmentManager();

        initView();

    }

    /**
     * 初始化View.
     */
    public void initView(){

    	this.setOrientation(LinearLayout.VERTICAL);
        View contentView = View.inflate(context,R.layout.ab_view_tab_pager,null);
        tabLayout = (TabLayout)contentView.findViewById(R.id.tab_layout);
        viewPager = (AbViewPager)contentView.findViewById(R.id.tab_content_view_pager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(tabTextViewList == null){
                    return;
                }
                for(int i=0;i<tabTextViewList.size();i++){
                    TextView textView  = tabTextViewList.get(i);
                    if(position == i){
                        textView.setTextColor(tabTextColors[1]);
                    }else{
                        textView.setTextColor(tabTextColors[0]);
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        this.addView(contentView);


    }

    /**
     * 设置Tab文字颜色
     * @param tabTextColors
     */
    public void setTabTextColors(int[] tabTextColors){
        this.tabTextColors = tabTextColors;
    }

    /**
     * 设置Tab文字大小
     * @param textSize
     */
    public void setTabTextSize(int textSize){
        this.tabTextSize = textSize;
    }

    /**
     * 设置Tab布局背景
     * @param resId
     */
    public void setTabBackgroundResource(int resId){
        this.tabLayout.setBackgroundResource(resId);
    }


    /**
     * 设置数据
     * @param tabTextList
     * @param tabDrawableList
     * @param tabFragmentList
     */
    public void setTabs(List<String> tabTextList,List<Drawable[]> tabDrawableList,List<Fragment> tabFragmentList){
        String[] tabArray = new String[tabTextList.size()];
        int i = 0;
        for(String text:tabTextList){
            tabArray[i] = text;
            i++;
        }
        setTabs(tabArray,tabDrawableList,tabFragmentList);
    }

    /**
     * 设置数据
     * @param tabTextArray
     * @param tabDrawableList
     * @param tabFragmentList
     */
    public void setTabs(String[] tabTextArray,List<Drawable[]> tabDrawableList,List<Fragment> tabFragmentList){
        this.tabDrawableList = tabDrawableList;
        setTabs(tabTextArray,tabFragmentList);
    }

    /**
     * 设置数据
     * @param tabTextList
     * @param tabFragmentList
     */
    public void setTabs(List<String> tabTextList,List<Fragment> tabFragmentList){
        String[] tabTextArray = new String[tabTextList.size()];
        int i = 0;
        for(String text:tabTextList){
            tabTextArray[i] = text;
            i++;
        }
        setTabs(tabTextArray,tabFragmentList);
    }

    /**
     * 设置数据
     * @param tabTextArray
     * @param tabFragmentList
     */
    public void setTabs(String[] tabTextArray,List<Fragment> tabFragmentList){
        this.tabTextArray = tabTextArray;
        this.tabFragmentList = tabFragmentList;
        this.tabTextViewList = new ArrayList<TextView>();
        fragmentPagerAdapter = new AbFragmentPagerAdapter(fragmentManager,tabTextArray, tabFragmentList);

        //viewpager加载adapter
        viewPager.setAdapter(fragmentPagerAdapter);

        //TabLayout加载viewpager
        //tabLayout.setViewPager(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(tabIndicatorColor);
        //为TabLayout添加tab名称
        for(int i=0;i<tabTextArray.length;i++){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(getTabView(i));
        }

    }

    /**
     * 添加getTabView的方法，来进行自定义Tab的布局View
     * @param position
     * @return
     */
    public View getTabView(int position){
        View view = View.inflate(context,R.layout.ab_item_tab_view,null);
        TextView tabText = (TextView)view.findViewById(R.id.tab_text);
        tabText.setText(this.tabTextArray[position]);
        tabText.setTextSize(TypedValue.COMPLEX_UNIT_SP,this.tabTextSize);
        this.tabTextViewList.add(tabText);

        ImageView tabIcon = (ImageView)view.findViewById(R.id.tab_icon);
        if(this.tabDrawableList!=null && this.tabDrawableList.size() == this.tabTextArray.length){
            tabIcon.setVisibility(View.VISIBLE);
            Drawable[] drawables = tabDrawableList.get(position);
            if(position == 0){
                tabIcon.setImageDrawable(drawables[1]);
            }else{
                tabIcon.setImageDrawable(drawables[0]);
            }
        }else{
            tabIcon.setVisibility(View.GONE);
        }

        if(tabTextColors==null || tabTextColors.length<2){
            tabTextColors = new int[]{AbColorUtil.getAttrColor(context,R.attr.colorAccent),context.getResources().getColor(R.color.app_text_light)};
        }

        if(position == 0){
            tabText.setTextColor(tabTextColors[1]);
        }else{
            tabText.setTextColor(tabTextColors[0]);
        }
        return view;
    }

	/**
	 * 获取ViewPager.
	 *
	 * @return the view pager
	 */
	public AbViewPager getViewPager() {
		return viewPager;
	}

    /**
     * 获取TabLayout
     * @return
     */
    public TabLayout getTabLayout() {
        return tabLayout;
    }


    /**
	 * 设置是否可以滑动控制.
	 * @param enabled
	 */
	public void setPagingEnabled(boolean enabled) {
		viewPager.setPagingEnabled(enabled);
	}


    /**
     * 设置TAB 模式.
     * @param mode  TabLayout.MODE_FIXED,TabLayout.MODE_SCROLLABLE
     */
    public void setTabMode(int mode) {
        tabLayout.setTabMode(mode);
    }

    /**
     * 滑条的颜色.
     * @param tabIndicatorColor
     */
    public void setTabIndicatorColor(int tabIndicatorColor) {
        this.tabIndicatorColor = tabIndicatorColor;
        this.tabLayout.setSelectedTabIndicatorColor(this.tabIndicatorColor);
    }

    /**
     * 让线和文字一样长度
     * @param tabLayout
     */
    public void setEqualMargin(final TabLayout tabLayout){
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);

                    for (int i = 0; i < linearLayout.getChildCount(); i++) {
                        View tabView = linearLayout.getChildAt(i);
                        TextView textView = tabView.findViewById(R.id.tab_text);
                        tabView.setPadding(0, 0, 0, 0);

                        int width = 0;
                        int maxWidth = 0;
                        width = textView.getWidth();
                        maxWidth = linearLayout.getWidth();
                        if (width == 0) {
                            textView.measure(0, 0);
                            linearLayout.measure(0, 0);
                            width = textView.getMeasuredWidth();
                            maxWidth = linearLayout.getMeasuredWidth();
                        }

                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width ;
                        params.leftMargin = (int)((maxWidth/2 - width)/2.0f);
                        params.rightMargin = (int)((maxWidth/2 - width)/2.0f);
                        tabView.setLayoutParams(params);

                        tabView.invalidate();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
