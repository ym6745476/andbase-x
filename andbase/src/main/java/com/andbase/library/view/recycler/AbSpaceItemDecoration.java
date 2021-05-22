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
 * Info  网格 RecyclerView 边距设置
 */

public class AbSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private Context context;
    private int space;
    private int column;
    private boolean hasHeader;
    private boolean hasFooter;

    /**1行的第1个和最后一个要不要边距*/
    private boolean leftRightPadding = false;

    public AbSpaceItemDecoration(Context context,int space, int column) {
        this.context = context;
        this.space = (int) AbViewUtil.dip2px(context,space);
        this.column = column;
        this.hasHeader = false;
    }

    public AbSpaceItemDecoration(Context context,int space, int column,boolean hasHeader,boolean hasFooter) {
        this.context = context;
        this.space = (int) AbViewUtil.dip2px(context,space);
        this.column = column;
        this.hasHeader = hasHeader;
        this.hasFooter = hasFooter;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = space/2;
        outRect.bottom = space/2;
        int position = parent.getChildLayoutPosition(view);
        if(hasHeader){
            if(position == 0){
                outRect.top = 0;
                outRect.left = 0;
                outRect.right = 0;
                outRect.bottom = 0;
            }else{
                if(leftRightPadding){
                    if((position-1)%column == 0){
                        outRect.left = space;
                        outRect.right = space/2;
                    }else if((position-1)%column == column-1){
                        outRect.left = space/2;
                        outRect.right = space;
                    }else{
                        outRect.left = space/2;
                        outRect.right = space/2;
                    }

                }else{
                    outRect.left = space/2;
                    outRect.right = space/2;
                }
            }

        }else{
            if(leftRightPadding){
                if(position%column == 0){
                    outRect.left = space;
                    outRect.right = space/2;
                }else if(position%column == column-1){
                    outRect.left = space/2;
                    outRect.right = space;
                }else{
                    outRect.left = space/2;
                    outRect.right = space/2;
                }

            }else{
                outRect.left = space/2;
                outRect.right = space/2;
            }

        }

        //最后一个是footer
        if(hasFooter){
            if(view instanceof AbFooterView){
                outRect.left = 0;
                outRect.right = 0;
                outRect.bottom = 0;
            }
        }

    }

    public void setLeftRightPadding(boolean hasLeftRightPadding) {
        this.leftRightPadding = hasLeftRightPadding;
    }
}
