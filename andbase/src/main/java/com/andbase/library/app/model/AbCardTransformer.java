package com.andbase.library.app.model;

import androidx.viewpager.widget.ViewPager;
import android.view.View;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2018/5/17 13:27
 * Email 396196516@qq.com
 * Info 卡片效果Transformer
 */
public class AbCardTransformer implements ViewPager.PageTransformer {

    private int mOffset = 60;

    @Override
    public void transformPage(View page, float position) {

        if (position <= 0) {
            page.setRotation(45 * position);
            page.setTranslationX((page.getWidth() / 2 * position));
        } else {
            //移动X轴坐标，使得卡片在同一坐标
            page.setTranslationX(-position * page.getWidth());
            //缩放卡片并调整位置
            float scale = (page.getWidth() - mOffset * position) / page.getWidth();
            page.setScaleX(scale);
            page.setScaleY(scale);
            //移动Y轴坐标
            page.setTranslationY(position * mOffset);
        }

    }
}
