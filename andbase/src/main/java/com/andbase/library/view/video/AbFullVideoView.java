package com.andbase.library.view.video;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class AbFullVideoView extends VideoView {

    public AbFullVideoView(Context context) {
        super(context);
    }

    public AbFullVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbFullVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}