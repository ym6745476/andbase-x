package com.andbase.library.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info Color工具类
 */
public class AbColorUtil {

    /**
     * 从主题的attr中获取颜色值
     * @param attrId
     * @return
     */
    public static int getAttrColor(Context context,int attrId){
        try{
            TypedArray array = context.getTheme().obtainStyledAttributes(new int[] {attrId});
            int color = array.getColor(0, 0x2AB4FB);
            array.recycle();
            return color;
        }catch(Exception e){
            return 0x2AB4FB;
        }
    }

    public static Drawable getAttrDrawable(Context context, int attrId){
        try{
            TypedArray array = context.getTheme().obtainStyledAttributes(new int[] {attrId});
            int color = array.getColor(0, 0x2AB4FB);
            array.recycle();
            return new ColorDrawable(color);
        }catch(Exception e){
            return null;
        }
    }

    public static ColorStateList getColorStateList(Context context,int attrId){
        int tintColor = getAttrColor(context,attrId);
       return generateThumbColorWithTintColor(tintColor);
    }


    public static ColorStateList generateThumbColorWithTintColor(final int tintColor) {

        int[][] states = new int[][]{
                {-android.R.attr.state_enabled, android.R.attr.state_checked},
                {-android.R.attr.state_enabled},
                {android.R.attr.state_pressed, -android.R.attr.state_checked},
                {android.R.attr.state_pressed, android.R.attr.state_checked},
                {android.R.attr.state_checked},
                {-android.R.attr.state_checked}
        };

        int[] colors = new int[]{
                tintColor - 0xAA000000,
                0xFFBABABA,
                tintColor - 0x99000000,
                tintColor - 0x99000000,
                tintColor | 0xFF000000,
                0x90757575   //一般状态
        };
        return new ColorStateList(states, colors);
    }

    public static ColorStateList generateBackColorWithTintColor(final int tintColor) {
        int[][] states = new int[][]{
                {-android.R.attr.state_enabled, android.R.attr.state_checked},
                {-android.R.attr.state_enabled},
                {android.R.attr.state_checked, android.R.attr.state_pressed},
                {-android.R.attr.state_checked, android.R.attr.state_pressed},
                {android.R.attr.state_checked},
                {-android.R.attr.state_checked}
        };

        int[] colors = new int[]{
                tintColor - 0xE1000000, //不可用
                0x20000000,             //不可用
                tintColor - 0xD0000000, //右边 按下
                0x80757575,             //左边 按下
                tintColor - 0x96000000, //选中右边背景
                0x809E9E9E              //选中左边背景
        };
        return new ColorStateList(states, colors);
    }
}
