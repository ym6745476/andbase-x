package com.andbase.library.view.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.andbase.library.R;
import com.andbase.library.utils.AbViewUtil;
import com.andbase.library.view.imageview.AbAspectRatioImageView;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/12/13 09:13
 * Email 396196516@qq.com
 * Info 圆角ImageView
 */
public class AbRoundAspectRadioImageView extends AbAspectRatioImageView {

    private Context context;
    private float width,height;
    private float radius = 0;

    public AbRoundAspectRadioImageView(Context context) {
        this(context, null);
    }

    public AbRoundAspectRadioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AbRoundImageView);
        try {
            int radiusDip = typedArray.getInteger(R.styleable.AbRoundImageView_radius, 0);
            radius = AbViewUtil.dip2px(context,radiusDip);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (width > radius && height > radius) {
            Path path = new Path();
            path.moveTo(radius, 0);
            path.lineTo(width - radius, 0);
            path.quadTo(width, 0, width, radius);
            path.lineTo(width, height - radius);
            path.quadTo(width, height, width - radius, height);
            path.lineTo(radius, height);
            path.quadTo(0, height, 0, height - radius);
            path.lineTo(0, radius);
            path.quadTo(0, 0, radius, 0);
            canvas.clipPath(path);
        }

        super.onDraw(canvas);
    }
}