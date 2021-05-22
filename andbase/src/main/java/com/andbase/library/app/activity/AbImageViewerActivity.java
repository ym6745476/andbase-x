package com.andbase.library.app.activity;


import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andbase.library.R;

import com.andbase.library.app.base.AbBaseActivity;
import com.andbase.library.view.photo.AbPhotoImageViewPager;
import com.andbase.library.view.photo.AbPhotoImageViewPagerAdapter;

import java.util.List;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/16 13:27
 * Email 396196516@qq.com
 * Info 图片浏览
 */
public class AbImageViewerActivity extends AbBaseActivity {

    private AbPhotoImageViewPager photoImageViewPager = null;
    private AbPhotoImageViewPagerAdapter photoImageViewPagerAdapter = null;
    private List<String> imageList = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View contentView = View.inflate(this,R.layout.ab_activity_image_viewer,null);
        setContentView(contentView);

        imageList = getIntent().getStringArrayListExtra("PATH");
        int position = getIntent().getIntExtra("POSITION",0);
        boolean screenOn = getIntent().getBooleanExtra("SCREEN_ON",false);
        if(screenOn){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        photoImageViewPager = (AbPhotoImageViewPager) contentView.findViewById(R.id.view_pager);
        photoImageViewPagerAdapter = new AbPhotoImageViewPagerAdapter(this,photoImageViewPager,imageList);
        photoImageViewPager.setAdapter(photoImageViewPagerAdapter);
        final TextView  imageCount = (TextView) contentView.findViewById(R.id.image_count);
        imageCount.setText((position+1)+"/"+imageList.size());

        ImageView backBtn = (ImageView) contentView.findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        photoImageViewPager.setCurrentItem(position);

        photoImageViewPager.setOnPageChangeListener(new AbPhotoImageViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                imageCount.setText((i+1)+"/"+imageList.size());
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

	}

    @Override
    public void finish() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.finish();
    }
}
