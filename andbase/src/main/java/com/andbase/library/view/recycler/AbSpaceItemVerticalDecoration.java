package com.andbase.library.view.recycler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.andbase.library.utils.AbViewUtil;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2017/6/16 13:27
 * Email 396196516@qq.com
 * Info  垂直 RecyclerView 边距设置
 */

public class AbSpaceItemVerticalDecoration extends RecyclerView.ItemDecoration {

    private Context context;
    private int space = -1;
    private int topBottomPadding = -1;
    private Paint paint;

    public AbSpaceItemVerticalDecoration(Context context, int space) {
        this.context = context;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if(space == 1){
            this.space = 1;
            paint.setColor(Color.parseColor("#EAEAEA"));
        }else if(space > 0){
            this.space = (int) AbViewUtil.dip2px(context,space);
            paint.setColor(Color.parseColor("#FAFAFA"));
        }
        paint.setStyle(Paint.Style.FILL);

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = 0;
        outRect.right = 0;
        int position = parent.getChildLayoutPosition(view);
        if(topBottomPadding == -1){
            outRect.top = 0;
            if(position  == parent.getAdapter().getItemCount() - 1){
                outRect.bottom = 0;
            }else{
                outRect.bottom = space;
            }

        }else{
            outRect.top = topBottomPadding;
            outRect.bottom = topBottomPadding;
        }

        //最后一个是footer
        if(view instanceof AbFooterView){
            outRect.left = 0;
            outRect.right = 0;
            outRect.bottom = 0;
        }
    }

    //绘制分割线
    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
        if(space > 0){
            drawVertical(canvas, parent);
        }
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
        final int childSize = parent.getChildCount();


        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            final int bottom = top + space;
            if (paint != null  && !(child instanceof AbFooterView)) {
                canvas.drawRect(left, top, right, bottom, paint);

                if(i == 0){
                    //多绘制顶部的间隔
                    if(topBottomPadding == -1){
                        canvas.drawRect(left, 0, right, space/2, paint);
                    }else{
                        canvas.drawRect(left, 0, right, topBottomPadding, paint);
                    }

                }
            }

        }
    }

    public int getTopBottomPadding() {
        return topBottomPadding;
    }

    public void setTopBottomPadding(int topBottomPadding) {
        this.topBottomPadding = topBottomPadding;
    }

}
