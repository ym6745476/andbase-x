package com.andbase.library.view.recycler;

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.andbase.library.utils.AbViewUtil;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2017/6/16 13:27
 * Email 396196516@qq.com
 * Info  水平 RecyclerView 边距设置
 */

public class AbSpaceItemHorizontalDecoration extends RecyclerView.ItemDecoration {

    private Context context;
    private int space;
    private int topBottomPadding = -1;

    public AbSpaceItemHorizontalDecoration(Context context,int space) {
        this.context = context;
        this.space = (int) AbViewUtil.dip2px(context,space);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space/2;
        outRect.right = space/2;

        if(topBottomPadding == -1){
            outRect.top = space/2;
            outRect.bottom = space/2;
        }else{
            outRect.top = topBottomPadding;
            outRect.bottom = topBottomPadding;
        }
    }

    public int getTopBottomPadding() {
        return topBottomPadding;
    }

    public void setTopBottomPadding(int topBottomPadding) {
        this.topBottomPadding = topBottomPadding;
    }
}
