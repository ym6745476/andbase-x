package com.andbase.library.view.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.andbase.library.R;
/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/12/13 09:13
 * Email 396196516@qq.com
 * Info 可设置宽高比的ImageView
 */
public class AbAspectRatioImageView extends AppCompatImageView {

    private int defaultWidthRatio   = 3;
    private int defaultHeightRatio  = 2;

    private int widthRatio;
    private int heightRatio;

    public AbAspectRatioImageView(Context context) {
        this(context, null);
    }

    public AbAspectRatioImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbAspectRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AbAspectRatioImageView);
        try {
            widthRatio = a.getInteger(R.styleable.AbAspectRatioImageView_width_ratio, 0);
            heightRatio = a.getInteger(R.styleable.AbAspectRatioImageView_height_ratio, 0);

            if (widthRatio == 0 || heightRatio == 0) {
                widthRatio = defaultWidthRatio;
                heightRatio = defaultHeightRatio;
            }
        } finally {
            a.recycle();
        }
    }

    /**
     * Sets width ratio.
     *
     * @param widthRatio the width ratio
     */
    public void setWidthRatio(int widthRatio) {
        this.widthRatio = widthRatio;
    }

    /**
     * Sets height ratio.
     *
     * @param heightRatio the height ratio
     */
    public void setHeightRatio(int heightRatio) {
        this.heightRatio = heightRatio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(widthMeasureSpec) * heightRatio / widthRatio;
        int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
