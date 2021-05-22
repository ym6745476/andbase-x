package com.andbase.library.app.model;

import androidx.viewpager.widget.ViewPager;
import android.view.View;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2018/5/17 13:27
 * Email 396196516@qq.com
 * Info 画廊效果Transformer（半透明+缩放）
 *
 * viewPagerAdapter = new AbViewPagerAdapter(this, items);
 * viewPager.setOffscreenPageLimit(3);
 * viewPager.setPageMargin(20);
 * viewPager.setAdapter(viewPagerAdapter);
 * viewPager.setPageTransformer(false, new AbGalleryTransformer());
 *
 *
 *<RelativeLayout
 * android:layout_width="match_parent"
 * android:layout_height="match_parent"
 * android:clipChildren="false">

 * <android.support.v4.view.ViewPager
 * android:id="@+id/view_pager"
 * android:layout_width="match_parent"
 * android:layout_height="match_parent"
 * android:layout_marginLeft="40dip"
 * android:layout_marginRight="40dip"
 * android:layout_marginTop="30dip"
 * android:layout_marginBottom="50dip"
 * android:layout_centerInParent="true"
 * android:clipChildren="false" />
 * </RelativeLayout>
 *
 *
 */
public class AbGalleryTransformer implements ViewPager.PageTransformer {

    private static final float MAX_ALPHA = 0.5f;
    private static final float MAX_SCALE = 0.9f;

    @Override
    public void transformPage(View page, float position) {
        if(position<-1||position>1){
            //不可见区域
            page.setAlpha(MAX_ALPHA);
            page.setScaleX(MAX_SCALE);
            page.setScaleY(MAX_SCALE);
        }else {
            //透明度效果
            if(position<=0){
                //pos区域[-1,0)
                page.setAlpha(MAX_ALPHA+MAX_ALPHA*(1+position));
            }else{
                //pos区域[0,1]
                page.setAlpha(MAX_ALPHA+MAX_ALPHA*(1-position));
            }
            //缩放效果
            float scale=Math.max(MAX_SCALE,1-Math.abs(position));
            page.setScaleX(scale);
            page.setScaleY(scale);
        }
    }
}
