package com.andbase.library.view.tabs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.andbase.library.R;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2018/5/17 17:54
 * Email 396196516@qq.com
 * Info CollapsingToolbarLayout + TabLayout.
 */

public class AbCoordinatorTabLayout extends CoordinatorLayout {

    private Context context;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private OnTabSelectedListener onTabSelectedListener;
    private FrameLayout headerView;

    public static int EXPANDED = 0;
    public static int COLLAPSED = 1;
    public static int IDLE = 2;

    public ImageView backBtn;
    public TextView titleView;


    public AbCoordinatorTabLayout(Context context) {
        super(context);
        this.context = context;
    }

    public AbCoordinatorTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (!isInEditMode()) {
            initView(context);
            initWidget(context, attrs);
        }
    }

    public AbCoordinatorTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        if (!isInEditMode()) {
            initView(context);
            initWidget(context, attrs);
        }
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.ab_view_coordinator_tab_layout, this, true);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((AppCompatActivity) context).setSupportActionBar(toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        headerView = (FrameLayout) findViewById(R.id.header_view);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        appBarLayout = (AppBarLayout)findViewById(R.id.app_bar_layout);
        backBtn = (ImageView) findViewById(R.id.back_btn);
        titleView = (TextView)this.findViewById(R.id.title_text);
    }

    private void initWidget(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs
                , R.styleable.AbCoordinatorTabLayout);

        TypedValue typedValue = new TypedValue();
        int contentScrimColor = typedArray.getColor(
                R.styleable.AbCoordinatorTabLayout_contentScrim, typedValue.data);
        collapsingToolbarLayout.setContentScrimColor(contentScrimColor);

        int tabIndicatorColor = typedArray.getColor(R.styleable.AbCoordinatorTabLayout_tabIndicatorColor, Color.WHITE);
        tabLayout.setSelectedTabIndicatorColor(tabIndicatorColor);

        int tabTextColor = typedArray.getColor(R.styleable.AbCoordinatorTabLayout_tabTextColor, Color.WHITE);
        tabLayout.setTabTextColors(ColorStateList.valueOf(tabTextColor));
        typedArray.recycle();
    }


    /**
     * 设置Toolbar标题
     * @param title 标题
     * @return
     */
    public AbCoordinatorTabLayout setTitle(String title) {
        if (titleView != null) {
            titleView.setText(title);
        }
        return this;
    }


    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (onTabSelectedListener != null) {
                    onTabSelectedListener.onTabSelected(tab);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (onTabSelectedListener != null) {
                    onTabSelectedListener.onTabUnselected(tab);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (onTabSelectedListener != null) {
                    onTabSelectedListener.onTabReselected(tab);
                }
            }
        });
    }

    /**
     * 设置TabLayout TabMode
     * @param mode
     * @return
     */
    public AbCoordinatorTabLayout setTabMode(@TabLayout.Mode int mode) {
        tabLayout.setTabMode(mode);
        return this;
    }

    /**
     * 设置与该组件搭配的ViewPager
     * @param viewPager 与TabLayout结合的ViewPager
     * @return
     */
    public AbCoordinatorTabLayout setupWithViewPager(ViewPager viewPager) {
        setupTabLayout();
        tabLayout.setupWithViewPager(viewPager);
        return this;
    }

    public TabLayout getTabLayout() {
        return tabLayout;
    }

    public AbCoordinatorTabLayout addOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        this.onTabSelectedListener = onTabSelectedListener;
        return this;
    }

    public void setTabLayout(TabLayout tabLayout) {
        this.tabLayout = tabLayout;
    }

    public AppBarLayout getAppBarLayout() {
        return appBarLayout;
    }

    public void setAppBarLayout(AppBarLayout appBarLayout) {
        this.appBarLayout = appBarLayout;
    }

    public CollapsingToolbarLayout getCollapsingToolbarLayout() {
        return collapsingToolbarLayout;
    }

    public void setCollapsingToolbarLayout(CollapsingToolbarLayout collapsingToolbarLayout) {
        this.collapsingToolbarLayout = collapsingToolbarLayout;
    }

    public FrameLayout getHeaderView() {
        return headerView;
    }

    public void setHeaderView(FrameLayout headerView) {
        this.headerView = headerView;
    }

    public interface OnTabSelectedListener {

        public void onTabSelected(TabLayout.Tab tab);

        public void onTabUnselected(TabLayout.Tab tab);

        public void onTabReselected(TabLayout.Tab tab);
    }

}