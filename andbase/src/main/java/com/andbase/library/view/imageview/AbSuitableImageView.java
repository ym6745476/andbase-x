package com.andbase.library.view.imageview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2018/6/4 09:13
 * Email 396196516@qq.com
 * Info 根据宽度自适应的ImageView
 */
public class AbSuitableImageView extends AppCompatImageView {

    public AbSuitableImageView(Context context) {
        super(context);
    }

    public AbSuitableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        Drawable d = getDrawable();
        if(d!= null){
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
            setMeasuredDimension(width, height);
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}
